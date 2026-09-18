package org.example.apigateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sharedmodule.api_gateway.event.EmailVerificationEvent;
import org.example.sharedmodule.api_gateway.event.PasswordChangedEvent;
import org.example.sharedmodule.api_gateway.event.PasswordResetEvent;
import org.example.sharedmodule.api_gateway.dto.CreateUserRequest;
import org.example.sharedmodule.general_exceptions.BusinessException;
import org.example.sharedmodule.user_service.exception.EmailAlreadyExistsException;
import org.example.apigateway.client.UserServiceClient;
import org.example.apigateway.dto.*;
import org.example.apigateway.metrics.GatewayMetrics;
import org.example.apigateway.model.AuthCredential;
import org.example.apigateway.model.RefreshToken;
import org.example.apigateway.model.Role;
import org.example.apigateway.repository.AuthCredentialRepository;
import org.example.outbox.service.OutboxWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.example.sharedmodule.Constants.TOPIC_NAMES.AUTH_EMAIL_VERIFICATION;
import static org.example.sharedmodule.Constants.TOPIC_NAMES.AUTH_PASSWORD_CHANGE;
import static org.example.sharedmodule.Constants.TOPIC_NAMES.AUTH_PASSWORD_RESET;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;
    private final AuthCredentialRepository credentialRepository;
    private final OutboxWriter outboxWriter;
    private final PlatformTransactionManager transactionManager;
    private final GatewayMetrics gatewayMetrics;

    @Value("${app.generated-email-domain:imdb.com}")
    private String generatedEmailDomain;

    // ── REGISTER ──────────────────────────────────────────────────────────────

    @Transactional
    public RegisterResponse register(RegisterRequest req) {
        if (credentialRepository.existsByEmail(req.email())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }
        AuthCredential credential = buildCredential(req);
        credentialRepository.save(credential);
        userServiceClient.registerUser(
            new CreateUserRequest(credential.getUserId(), req.email(), req.username(), credential.getPasswordHash())
        );
        sendVerificationNotification(credential);
        return new RegisterResponse(credential.getUserId(), "Registration successful. Check your email to verify your account.");
    }

    // ── LOGIN ─────────────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse login(LoginRequest req) {
        AuthCredential credential = getCredentialByEmail(req.email());
        assertLoginAllowed(credential);

        if (!passwordEncoder.matches(req.password(), credential.getPasswordHash())) handleFailedAttempt(credential);
        if (hasPriorFailures(credential)) resetFailedAttempts(credential);
        credential.setLastLogin(Instant.now());
        credentialRepository.save(credential);
        return buildAuthResponse(credential, authTokenService.createNewRefreshToken(credential.getUserId()));
    }

    // ── REFRESH ───────────────────────────────────────────────────────────────

    public AuthResponse refresh(String refreshToken) {
        RefreshToken newToken = authTokenService.refresh(refreshToken);
        AuthCredential credential = getCredentialByUserId(newToken.getUserId());
        assertLoginAllowed(credential);
        return buildAuthResponse(credential, newToken);
    }

    private AuthResponse buildAuthResponse(AuthCredential credential, RefreshToken refreshToken) {
        UUID userId = credential.getUserId();
        List<String> roles = List.of(credential.getRole().name());
        String access = authTokenService.generateAccessToken(userId, credential.getEmail(), roles, refreshToken.getSessionId());
        String refresh = authTokenService.generateRefreshToken(userId, refreshToken.getSessionId());
        return new AuthResponse(access, refresh);
    }

    // ── LOGOUT ────────────────────────────────────────────────────────────────

    public void logout(String accessToken) {
        authTokenService.invalidateSession(accessToken);
        SecurityContextHolder.clearContext();
    }

    public void logoutFromAllDevices(UUID userId) {
        authTokenService.invalidateAllSession(userId);
        SecurityContextHolder.clearContext();
    }

    // ── EMAIL VERIFY ──────────────────────────────────────────────────────────

    @Transactional
    public void triggerVerificationEmail(String email) {
        AuthCredential credential = getCredentialByEmail(email);
        if (Boolean.TRUE.equals(credential.getIsVerified())) return;
        sendVerificationNotification(credential);
    }

    @Transactional
    public void verifyEmail(String otp) {
        UUID userId = authTokenService.validateEmailOtp(otp);
        AuthCredential credential = getCredentialByUserId(userId);
        credential.setIsVerified(true);
        credentialRepository.save(credential);
    }

    // ── FORGOT / RESET PASSWORD ───────────────────────────────────────────────

    @Transactional
    public void triggerPasswordReset(String email) {
        AuthCredential credential = getCredentialByEmail(email);
        String otp = authTokenService.generatePasswordResetOtp(credential.getUserId());
        outboxWriter.save(new PasswordResetEvent(UUID.randomUUID(), credential.getUserId(), otp),
            AUTH_PASSWORD_RESET, credential.getUserId().toString());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        try {
            UUID userId = authTokenService.validatePasswordResetOtp(req.otp());
            AuthCredential credential = getCredentialByUserId(userId);
            assertLoginAllowed(credential);
            credential.setPasswordHash(passwordEncoder.encode(req.newPassword()));
            credentialRepository.save(credential);
            logoutFromAllDevices(userId);
        } catch (BusinessException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new BusinessException("Invalid or expired reset OTP", HttpStatus.BAD_REQUEST);
        }
    }

    // ── CHANGE PASSWORD ───────────────────────────────────────────────────────

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest req) {
        AuthCredential credential = getCredentialByUserId(userId);
        assertLoginAllowed(credential);

        if (!passwordEncoder.matches(req.currentPassword(), credential.getPasswordHash()))
            throw new BusinessException("Current password is incorrect", HttpStatus.UNAUTHORIZED);

        credential.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        credentialRepository.save(credential);
        logoutFromAllDevices(userId);
        outboxWriter.save(new PasswordChangedEvent(UUID.randomUUID(), userId),
            AUTH_PASSWORD_CHANGE, userId.toString());
    }

    // ── DEACTIVATE ACCOUNT ────────────────────────────────────────────────────

    @Transactional
    public void deactivateAccount(UUID userId) {
        AuthCredential credential = getCredentialByUserId(userId);
        assertLoginAllowed(credential);
        credential.setIsActive(false);
        credentialRepository.save(credential);
        logoutFromAllDevices(userId);
        SecurityContextHolder.clearContext();
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private AuthCredential getCredentialByEmail(String email) {
        return credentialRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException("Account not found", HttpStatus.NOT_FOUND));
    }

    private AuthCredential getCredentialByUserId(UUID userId) {
        return credentialRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException("Account not found", HttpStatus.NOT_FOUND));
    }

    private void assertLoginAllowed(AuthCredential c) {
        if (!Boolean.TRUE.equals(c.getIsVerified())) {
            gatewayMetrics.recordAuthFailure("not_verified");
            throw new BusinessException("Account is not verified", HttpStatus.UNAUTHORIZED);
        }

        if (!Boolean.TRUE.equals(c.getIsActive())) {
            gatewayMetrics.recordAuthFailure("not_active");
            throw new BusinessException("Account is not active. Contact support.", HttpStatus.UNAUTHORIZED);
        }

        if (Boolean.TRUE.equals(c.getLocked())) {
            boolean lockedDueToFailedAttempts = c.getLockedUntil() != null
                && c.getLockedUntil().isAfter(Instant.now())
                && c.getLockedUntil().isBefore(Instant.now().plus(Duration.ofHours(1)));

            if (lockedDueToFailedAttempts) {
                String failedLoginMessage = "Account is locked until " + c.getLockedUntil()
                    + ". Because of multiple failed login attempts.";
                gatewayMetrics.recordAuthFailure("account_locked");
                throw new BusinessException(failedLoginMessage, HttpStatus.UNAUTHORIZED);
            }
            gatewayMetrics.recordAuthFailure("pending_approval");
            throw new BusinessException("Account is pending approval", HttpStatus.UNAUTHORIZED);
        }
    }

    private void handleFailedAttempt(AuthCredential c) {
        gatewayMetrics.recordAuthFailure("wrong_password");
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        tx.execute(status -> {
            c.setFailedAttempts(c.getFailedAttempts() == null ? 1 : c.getFailedAttempts() + 1);
            if (c.getFailedAttempts() >= 5) {
                c.setLocked(true);
                c.setLockedUntil(Instant.now().plus(2, ChronoUnit.MINUTES));
            }
            credentialRepository.save(c);
            return null;
        });
        throw new BusinessException("Wrong password, please try again", HttpStatus.UNAUTHORIZED);
    }

    private boolean hasPriorFailures(AuthCredential c) {
        return c.getFailedAttempts() != null && c.getFailedAttempts() > 0;
    }

    private void resetFailedAttempts(AuthCredential c) {
        c.setFailedAttempts(0);
        c.setLocked(false);
        c.setLockedUntil(null);
        credentialRepository.save(c);
    }

    private void sendVerificationNotification(AuthCredential credential) {
        String otp = authTokenService.generateEmailVerificationOtp(credential.getUserId());
        outboxWriter.save(
            new EmailVerificationEvent(UUID.randomUUID(), credential.getUserId(), credential.getEmail(), otp),
            AUTH_EMAIL_VERIFICATION, credential.getUserId().toString()
        );
    }

    private AuthCredential buildCredential(RegisterRequest req) {
        AuthCredential credential = AuthCredential.builder()
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .role(Role.USER)
                .isActive(true)
                .isVerified(false)
                .locked(false)
                .failedAttempts(0)
                .build();
        return credential;
    }

    private String generateRandomString(int length) {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom RANDOM = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private AuthCredential buildGeneratedCredential(String email, String rawPassword, Role role) {
        return AuthCredential.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .isActive(true)
                .isVerified(true)
                .locked(false)
                .build();
    }
}