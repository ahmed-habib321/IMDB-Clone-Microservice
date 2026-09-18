package org.example.apigateway.service;

import org.example.sharedmodule.api_gateway.event.EmailVerificationEvent;
import org.example.sharedmodule.api_gateway.event.PasswordChangedEvent;
import org.example.sharedmodule.api_gateway.event.PasswordResetEvent;
import org.example.sharedmodule.general_exceptions.BusinessException;
import org.example.apigateway.client.UserServiceClient;
import org.example.apigateway.dto.*;
import org.example.apigateway.metrics.GatewayMetrics;
import org.example.apigateway.model.AuthCredential;
import org.example.apigateway.model.RefreshToken;
import org.example.apigateway.model.Role;
import org.example.apigateway.repository.AuthCredentialRepository;
import org.example.outbox.service.OutboxWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock AuthTokenService authTokenService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock UserServiceClient userServiceClient;
    @Mock AuthCredentialRepository credentialRepository;
    @Mock OutboxWriter outboxWriter;
    @Mock PlatformTransactionManager transactionManager;
    @Mock GatewayMetrics gatewayMetrics;

    @InjectMocks AuthService authService;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SESSION_ID = UUID.randomUUID();

    private AuthCredential activeVerifiedUser() {
        return AuthCredential.builder()
            .userId(USER_ID)
            .email("test@test.com")
            .passwordHash("$2a$12$encodedpass")
            .role(Role.USER)
            .isActive(true)
            .isVerified(true)
            .locked(false)
            .failedAttempts(0)
            .build();
    }

    private RefreshToken mockRefreshToken() {
        RefreshToken rt = new RefreshToken();
        rt.setUserId(USER_ID);
        rt.setSessionId(SESSION_ID);
        rt.setRevoked(false);
        rt.setExpiryDate(Instant.now().plusSeconds(3600));
        return rt;
    }

    // ── Register ──────────────────────────────────────────────────────────────

    @Test
    void register_shouldReturnResponse_withUserId() {
        when(credentialRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$encoded");
        when(credentialRepository.save(any(AuthCredential.class)))
            .thenAnswer(inv -> {
                AuthCredential c = inv.getArgument(0);
                if (c.getUserId() == null) {
                    ReflectionTestUtils.setField(c, "userId", USER_ID);
                }
                return c;
            });
        when(authTokenService.generateEmailVerificationOtp(USER_ID)).thenReturn("123456");

        RegisterResponse response = authService.register(new RegisterRequest("test@test.com", "testuser", "password123"));

        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.message()).contains("verify your account");
        verify(credentialRepository).save(any(AuthCredential.class));
        verify(userServiceClient).registerUser(argThat(req -> req.userId().equals(USER_ID)));
        verify(outboxWriter).save(any(EmailVerificationEvent.class), anyString(), anyString());
    }

    @Test
    void register_shouldThrow_whenEmailExists() {
        when(credentialRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest("test@test.com", "testuser", "password123")))
            .isInstanceOf(RuntimeException.class);
        verify(userServiceClient, never()).registerUser(any());
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Test
    void login_shouldReturnTokens_forValidCredentials() {
        when(credentialRepository.findByEmail("test@test.com")).thenReturn(Optional.of(activeVerifiedUser()));
        when(passwordEncoder.matches("password123", "$2a$12$encodedpass")).thenReturn(true);
        when(authTokenService.createNewRefreshToken(USER_ID)).thenReturn(mockRefreshToken());
        when(authTokenService.generateAccessToken(any(), anyString(), anyList(), any())).thenReturn("access-token");
        when(authTokenService.generateRefreshToken(any(), any())).thenReturn("refresh-token");

        AuthResponse response = authService.login(new LoginRequest("test@test.com", "password123"));

        assertThat(response.access()).isEqualTo("access-token");
        assertThat(response.refresh()).isEqualTo("refresh-token");
    }

    @Test
    void login_shouldThrow_forInactiveUser() {
        AuthCredential inactive = activeVerifiedUser();
        inactive.setIsActive(false);
        when(credentialRepository.findByEmail("test@test.com")).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> authService.login(new LoginRequest("test@test.com", "password123")))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("not active");
    }

    @Test
    void login_shouldThrow_forUnverifiedUser() {
        AuthCredential unverified = activeVerifiedUser();
        unverified.setIsVerified(false);
        when(credentialRepository.findByEmail("test@test.com")).thenReturn(Optional.of(unverified));

        assertThatThrownBy(() -> authService.login(new LoginRequest("test@test.com", "password123")))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("not verified");
    }

    @Test
    void login_shouldThrow_forLockedAccount() {
        AuthCredential locked = activeVerifiedUser();
        locked.setLocked(true);
        locked.setLockedUntil(Instant.now().plusSeconds(120));
        when(credentialRepository.findByEmail("test@test.com")).thenReturn(Optional.of(locked));

        assertThatThrownBy(() -> authService.login(new LoginRequest("test@test.com", "password123")))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("locked");
    }

    @Test
    void login_shouldRecordFailedLogin_forWrongPassword() {
        when(credentialRepository.findByEmail("test@test.com")).thenReturn(Optional.of(activeVerifiedUser()));
        when(passwordEncoder.matches("wrongpass", "$2a$12$encodedpass")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("test@test.com", "wrongpass")))
            .isInstanceOf(BusinessException.class);

        verify(gatewayMetrics).recordAuthFailure("wrong_password");
        verify(authTokenService, never()).createNewRefreshToken(any());
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    @Test
    void logout_shouldInvalidateSession() {
        authService.logout("some-access-token");
        verify(authTokenService).invalidateSession("some-access-token");
    }

    @Test
    void logoutAll_shouldInvalidateAllSessions() {
        authService.logoutFromAllDevices(USER_ID);
        verify(authTokenService).invalidateAllSession(USER_ID);
    }

    // ── Email Verify ──────────────────────────────────────────────────────────

    @Test
    void triggerVerificationEmail_shouldPublishEvent() {
        AuthCredential unverified = activeVerifiedUser();
        unverified.setIsVerified(false);
        when(credentialRepository.findByEmail("test@test.com")).thenReturn(Optional.of(unverified));
        when(authTokenService.generateEmailVerificationOtp(USER_ID)).thenReturn("123456");

        authService.triggerVerificationEmail("test@test.com");

        verify(outboxWriter).save(any(EmailVerificationEvent.class), anyString(), anyString());
    }

    @Test
    void verifyEmail_shouldMarkVerified() {
        when(authTokenService.validateEmailOtp("123456")).thenReturn(USER_ID);
        when(credentialRepository.findByUserId(USER_ID)).thenReturn(Optional.of(activeVerifiedUser()));

        authService.verifyEmail("123456");

        verify(credentialRepository).save(argThat(c -> Boolean.TRUE.equals(c.getIsVerified())));
    }

    // ── Password Reset ────────────────────────────────────────────────────────

    @Test
    void triggerPasswordReset_shouldPublishEvent() {
        when(credentialRepository.findByEmail("test@test.com")).thenReturn(Optional.of(activeVerifiedUser()));
        when(authTokenService.generatePasswordResetOtp(USER_ID)).thenReturn("123456");

        authService.triggerPasswordReset("test@test.com");

        verify(outboxWriter).save(any(PasswordResetEvent.class), anyString(), anyString());
    }

    @Test
    void resetPassword_shouldUpdateHashAndPublishChangeEvent() {
        when(authTokenService.validatePasswordResetOtp("123456")).thenReturn(USER_ID);
        when(credentialRepository.findByUserId(USER_ID)).thenReturn(Optional.of(activeVerifiedUser()));
        when(passwordEncoder.encode("newpassword")).thenReturn("$2a$encoded-new");

        authService.resetPassword(new ResetPasswordRequest("123456", "newpassword"));

        verify(credentialRepository).save(argThat(c -> "$2a$encoded-new".equals(c.getPasswordHash())));
    }

    // ── Change Password ───────────────────────────────────────────────────────

    @Test
    void changePassword_shouldUpdateAndPublish() {
        when(credentialRepository.findByUserId(USER_ID)).thenReturn(Optional.of(activeVerifiedUser()));
        when(passwordEncoder.matches("oldpass", "$2a$12$encodedpass")).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("$2a$encoded-new");

        authService.changePassword(USER_ID, new ChangePasswordRequest("oldpass", "newpass"));

        verify(credentialRepository).save(argThat(c -> "$2a$encoded-new".equals(c.getPasswordHash())));
        verify(outboxWriter).save(any(PasswordChangedEvent.class), anyString(), anyString());
    }

    @Test
    void changePassword_shouldThrow_whenCurrentPasswordWrong() {
        when(credentialRepository.findByUserId(USER_ID)).thenReturn(Optional.of(activeVerifiedUser()));
        when(passwordEncoder.matches("wrong", "$2a$12$encodedpass")).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword(USER_ID, new ChangePasswordRequest("wrong", "newpass")))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Current password is incorrect");
    }

    // ── Login: additional good-code scenarios ────────────────────────────────

    @Test
    void login_shouldResetFailedAttempts_onSuccessfulLogin() {
        AuthCredential hasFailures = activeVerifiedUser();
        hasFailures.setFailedAttempts(3);
        when(credentialRepository.findByEmail("test@test.com")).thenReturn(Optional.of(hasFailures));
        when(passwordEncoder.matches("password123", "$2a$12$encodedpass")).thenReturn(true);
        when(authTokenService.createNewRefreshToken(USER_ID)).thenReturn(mockRefreshToken());
        when(authTokenService.generateAccessToken(any(), anyString(), anyList(), any())).thenReturn("access-token");
        when(authTokenService.generateRefreshToken(any(), any())).thenReturn("refresh-token");

        authService.login(new LoginRequest("test@test.com", "password123"));

        assertThat(hasFailures.getFailedAttempts()).isZero();
        assertThat(hasFailures.getLocked()).isFalse();
        assertThat(hasFailures.getLockedUntil()).isNull();
    }

    @Test
    void login_shouldLockAccount_after5FailedAttempts() {
        AuthCredential nearlyLocked = activeVerifiedUser();
        nearlyLocked.setFailedAttempts(4);
        when(credentialRepository.findByEmail("test@test.com")).thenReturn(Optional.of(nearlyLocked));
        when(passwordEncoder.matches("wrongpass", "$2a$12$encodedpass")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("test@test.com", "wrongpass")))
            .isInstanceOf(BusinessException.class);

        assertThat(nearlyLocked.getLocked()).isTrue();
        assertThat(nearlyLocked.getLockedUntil()).isNotNull();
        assertThat(nearlyLocked.getFailedAttempts()).isEqualTo(5);
    }

    @Test
    void login_shouldThrow_whenAccountNotFound() {
        when(credentialRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("nonexistent@test.com", "password")))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Account not found");
    }

    // ── Refresh: additional good-code scenarios ──────────────────────────────

    @Test
    void refresh_shouldReturnTokens_forValidRefresh() {
        RefreshToken rotated = mockRefreshToken();
        when(authTokenService.refresh("old-refresh-token")).thenReturn(rotated);
        when(credentialRepository.findByUserId(USER_ID)).thenReturn(Optional.of(activeVerifiedUser()));
        when(authTokenService.generateAccessToken(any(), anyString(), anyList(), any())).thenReturn("new-access");
        when(authTokenService.generateRefreshToken(any(), any())).thenReturn("new-refresh");

        AuthResponse response = authService.refresh("old-refresh-token");

        assertThat(response.access()).isEqualTo("new-access");
        assertThat(response.refresh()).isEqualTo("new-refresh");
    }

    @Test
    void refresh_shouldThrow_whenUserNotFound() {
        RefreshToken rotated = mockRefreshToken();
        rotated.setUserId(UUID.randomUUID());
        when(authTokenService.refresh("token")).thenReturn(rotated);
        when(credentialRepository.findByUserId(rotated.getUserId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh("token"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Account not found");
    }

    // ── Email Verify: additional good-code scenario ──────────────────────────

    @Test
    void triggerVerificationEmail_shouldSkip_whenAlreadyVerified() {
        when(credentialRepository.findByEmail("test@test.com")).thenReturn(Optional.of(activeVerifiedUser()));

        authService.triggerVerificationEmail("test@test.com");

        verify(outboxWriter, never()).save(any(), anyString(), anyString());
    }

    // ── Password Reset: additional good-code scenarios ───────────────────────

    @Test
    void resetPassword_shouldThrow_whenInvalidToken() {
        when(authTokenService.validatePasswordResetOtp("000000"))
            .thenThrow(new BusinessException("Invalid or expired password reset OTP", HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> authService.resetPassword(new ResetPasswordRequest("000000", "newPwd")))
            .isInstanceOf(BusinessException.class);
    }

    @Test
    void resetPassword_shouldWrapRuntimeException() {
        when(authTokenService.validatePasswordResetOtp("bad")).thenThrow(new RuntimeException("parse error"));

        assertThatThrownBy(() -> authService.resetPassword(new ResetPasswordRequest("bad", "newPwd")))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Invalid or expired reset OTP");
    }

    // ── Deactivate: additional good-code scenario ────────────────────────────

    @Test
    void deactivateAccount_shouldDeactivate_andInvalidateSessions() {
        when(credentialRepository.findByUserId(USER_ID)).thenReturn(Optional.of(activeVerifiedUser()));

        authService.deactivateAccount(USER_ID);

        verify(credentialRepository).save(argThat(c -> Boolean.FALSE.equals(c.getIsActive())));
        verify(authTokenService).invalidateAllSession(USER_ID);
    }
}
