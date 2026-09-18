package org.example.apigateway.service;

import io.jsonwebtoken.Claims;
import org.example.sharedmodule.general_exceptions.BusinessException;
import org.example.apigateway.model.RefreshToken;
import org.example.apigateway.model.TokenType;
import org.example.apigateway.repository.AccessTokenRepository;
import org.example.apigateway.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class AuthTokenServiceTest {

    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock AccessTokenRepository accessTokenRepository;
    @Mock StringRedisTemplate redis;
    @Mock ValueOperations<String, String> valueOps;

    @InjectMocks
    AuthTokenService authTokenService;

    private static final String SECRET = "test-secret-must-be-at-least-32-bytes-long!!";
    private static final UUID USER_ID = UUID.randomUUID();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(authTokenService, "secret", SECRET);
        ReflectionTestUtils.setField(authTokenService, "accessExpiry", 300_000L);
        ReflectionTestUtils.setField(authTokenService, "refreshExpiry", 604_800_000L);
        ReflectionTestUtils.setField(authTokenService, "emailExpiry", 300_000L);
        ReflectionTestUtils.setField(authTokenService, "passwordResetExpiry", 300_000L);
        ReflectionTestUtils.setField(authTokenService, "issuer", "test-issuer");
        authTokenService.init();

        when(redis.opsForValue()).thenReturn(valueOps);
    }

    @Test
    @DisplayName("The generated Access token should be valid")
    void generateAccessToken_shouldProduceValidToken() {
        UUID sessionId = UUID.randomUUID();

        String token = authTokenService.generateAccessToken(USER_ID, "test@test.com", List.of("USER"), sessionId);

        assertThat(token).isNotBlank();
        Claims claims = authTokenService.parseToken(token);
        assertThat(claims.getSubject()).isEqualTo(USER_ID.toString());
        assertThat(claims.get("type", String.class)).isEqualTo(TokenType.ACCESS.name());
        assertThat(claims.get("email", String.class)).isEqualTo("test@test.com");
        verify(accessTokenRepository).insertToRedis(eq(USER_ID.toString()), any(), any());
    }

    @Test
    @DisplayName("isAccessTokenValid returns true when the access token is in redis")
    void isAccessTokenValid_returnsTrue_whenTokenInRedis() {
        UUID sessionId = UUID.randomUUID();
        String token = authTokenService.generateAccessToken(USER_ID, "test@test.com", List.of("USER"), sessionId);

        when(accessTokenRepository.existsInRedis(anyString())).thenReturn(true);

        assertThat(authTokenService.isAccessTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("isAccessTokenValid returns false when the access token is not in redis")
    void isAccessTokenValid_returnsFalse_whenTokenNotInRedis() {
        UUID sessionId = UUID.randomUUID();
        String token = authTokenService.generateAccessToken(USER_ID, "test@test.com", List.of("USER"), sessionId);

        when(accessTokenRepository.existsInRedis(anyString())).thenReturn(false);

        assertThat(authTokenService.isAccessTokenValid(token)).isFalse();
    }

    @Test
    @DisplayName("isAccessTokenValid rejects a refresh token")
    void isAccessTokenValid_returnsFalse_forRefreshToken() {
        UUID sessionId = UUID.randomUUID();
        String refreshToken = authTokenService.generateRefreshToken(USER_ID, sessionId);

        when(accessTokenRepository.existsInRedis(anyString())).thenReturn(true);

        assertThat(authTokenService.isAccessTokenValid(refreshToken)).isFalse();
    }

    @Test
    @DisplayName("generate/validate email verification OTP round-trip")
    void emailVerificationOtp_roundTrip() {
        String otp = authTokenService.generateEmailVerificationOtp(USER_ID);
        assertThat(otp).matches("\\d{6}");

        when(valueOps.get("auth:otp:EMAIL:" + otp)).thenReturn(USER_ID.toString());

        UUID result = authTokenService.validateEmailOtp(otp);
        assertThat(result).isEqualTo(USER_ID);
        verify(redis).delete("auth:otp:EMAIL:" + otp);
    }

    @Test
    @DisplayName("validateEmailOtp throws for invalid OTP")
    void validateEmailOtp_shouldThrow_whenInvalid() {
        when(valueOps.get("auth:otp:EMAIL:000000")).thenReturn(null);

        assertThatThrownBy(() -> authTokenService.validateEmailOtp("000000"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Invalid or expired email verification OTP");
    }

    @Test
    @DisplayName("generate/validate password reset OTP round-trip")
    void passwordResetOtp_roundTrip() {
        String otp = authTokenService.generatePasswordResetOtp(USER_ID);
        assertThat(otp).matches("\\d{6}");

        when(valueOps.get("auth:otp:PASSWORD:" + otp)).thenReturn(USER_ID.toString());

        UUID result = authTokenService.validatePasswordResetOtp(otp);
        assertThat(result).isEqualTo(USER_ID);
        verify(redis).delete("auth:otp:PASSWORD:" + otp);
    }

    @Test
    @DisplayName("validatePasswordResetOtp throws for invalid OTP")
    void validatePasswordResetOtp_shouldThrow_whenInvalid() {
        when(valueOps.get("auth:otp:PASSWORD:000000")).thenReturn(null);

        assertThatThrownBy(() -> authTokenService.validatePasswordResetOtp("000000"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Invalid or expired password reset OTP");
    }

    @Test
    @DisplayName("refresh detects token reuse and invalidates all sessions")
    void refresh_shouldThrow_whenSessionRevoked() {
        UUID sessionId = UUID.randomUUID();
        String refreshJwt = authTokenService.generateRefreshToken(USER_ID, sessionId);

        RefreshToken revokedToken = RefreshToken.builder()
            .sessionId(sessionId)
            .userId(USER_ID)
            .revoked(true)
            .expiryDate(Instant.now().plusSeconds(3600))
            .build();

        when(refreshTokenRepository.findRefreshTokenBySessionId(sessionId))
            .thenReturn(Optional.of(revokedToken));

        assertThatThrownBy(() -> authTokenService.refresh(refreshJwt))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Refresh token reuse detected — all sessions invalidated");
    }

    @Test
    @DisplayName("refresh rejects an expired session")
    void refresh_shouldThrow_whenSessionExpired() {
        UUID sessionId = UUID.randomUUID();
        String refreshJwt = authTokenService.generateRefreshToken(USER_ID, sessionId);

        RefreshToken expiredToken = RefreshToken.builder()
            .sessionId(sessionId)
            .userId(USER_ID)
            .revoked(false)
            .expiryDate(Instant.now().minusSeconds(10))
            .build();

        when(refreshTokenRepository.findRefreshTokenBySessionId(sessionId))
            .thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> authTokenService.refresh(refreshJwt))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("expired");
    }

    @Test
    @DisplayName("invalidateAllSession deletes refresh tokens and redis access sessions")
    void invalidateAllSession_deletesRefreshTokensAndRedisSessions() {
        authTokenService.invalidateAllSession(USER_ID);

        verify(refreshTokenRepository).deleteByUserId(USER_ID);
        verify(accessTokenRepository).removeAllActiveSessionsFromRedis(USER_ID);
    }

    // ── Additional good-code scenarios ───────────────────────────────────────

    @Test
    @DisplayName("init throws when the JWT secret is too short")
    void init_shouldThrow_whenSecretTooShort() {
        AuthTokenService svc = new AuthTokenService(refreshTokenRepository, accessTokenRepository, redis);
        ReflectionTestUtils.setField(svc, "secret", "too-short");

        assertThatThrownBy(svc::init)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("at least 32 bytes");
    }

    @Test
    @DisplayName("createNewRefreshToken persists and returns the session")
    void createNewRefreshToken_shouldPersistAndReturn() {
        RefreshToken saved = RefreshToken.builder()
            .id(UUID.randomUUID())
            .sessionId(UUID.randomUUID())
            .userId(USER_ID)
            .revoked(false)
            .expiryDate(Instant.now().plusSeconds(3600))
            .build();
        when(refreshTokenRepository.save(any())).thenReturn(saved);

        assertThat(authTokenService.createNewRefreshToken(USER_ID)).isSameAs(saved);
    }

    @Test
    @DisplayName("generateRefreshToken produces a REFRESH-type JWT with sid claim")
    void generateRefreshToken_shouldEncodeSidAndType() {
        UUID sessionId = UUID.randomUUID();

        String token = authTokenService.generateRefreshToken(USER_ID, sessionId);
        Claims claims = authTokenService.parseToken(token);

        assertThat(claims.getSubject()).isEqualTo(USER_ID.toString());
        assertThat(claims.get("sid", String.class)).isEqualTo(sessionId.toString());
        assertThat(claims.get("type", String.class)).isEqualTo(TokenType.REFRESH.name());
    }

    @Test
    @DisplayName("refresh rotates the token: revokes the old session and returns a new one")
    void refresh_shouldRotateToken() {
        UUID oldSessionId = UUID.randomUUID();
        RefreshToken oldSession = RefreshToken.builder()
            .id(UUID.randomUUID())
            .sessionId(oldSessionId)
            .userId(USER_ID)
            .revoked(false)
            .expiryDate(Instant.now().plusSeconds(3600))
            .build();
        String oldJwt = authTokenService.generateRefreshToken(USER_ID, oldSessionId);

        RefreshToken newSession = RefreshToken.builder()
            .id(UUID.randomUUID())
            .sessionId(UUID.randomUUID())
            .userId(USER_ID)
            .revoked(false)
            .expiryDate(Instant.now().plusSeconds(3600))
            .build();

        when(refreshTokenRepository.findRefreshTokenBySessionId(oldSessionId)).thenReturn(Optional.of(oldSession));
        when(refreshTokenRepository.save(any())).thenReturn(newSession);

        RefreshToken result = authTokenService.refresh(oldJwt);

        assertThat(oldSession.isRevoked()).isTrue();
        assertThat(result).isSameAs(newSession);
    }

    @Test
    @DisplayName("refresh rejects a non-existent session")
    void refresh_shouldReject_whenSessionNotFound() {
        UUID oldSessionId = UUID.randomUUID();
        String oldJwt = authTokenService.generateRefreshToken(USER_ID, oldSessionId);

        when(refreshTokenRepository.findRefreshTokenBySessionId(oldSessionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authTokenService.refresh(oldJwt))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Invalid refresh token");
    }

    @Test
    @DisplayName("refresh rejects a wrong token type")
    void refresh_shouldReject_whenAccessTokenPassed() {
        UUID sessionId = UUID.randomUUID();
        String accessJwt = authTokenService.generateAccessToken(USER_ID, "test@test.com", List.of("USER"), sessionId);

        assertThatThrownBy(() -> authTokenService.refresh(accessJwt))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Invalid token type");
    }

    @Test
    @DisplayName("isAccessTokenValid returns false for an expired token")
    void isAccessTokenValid_shouldReturnFalse_forExpiredToken() {
        AuthTokenService svc = new AuthTokenService(refreshTokenRepository, accessTokenRepository, redis);
        ReflectionTestUtils.setField(svc, "secret", SECRET);
        ReflectionTestUtils.setField(svc, "accessExpiry", -1000L);
        ReflectionTestUtils.setField(svc, "refreshExpiry", 604_800_000L);
        ReflectionTestUtils.setField(svc, "emailExpiry", 300_000L);
        ReflectionTestUtils.setField(svc, "passwordResetExpiry", 300_000L);
        ReflectionTestUtils.setField(svc, "issuer", "test-issuer");
        svc.init();

        String token = svc.generateAccessToken(USER_ID, "test@test.com", List.of("USER"), UUID.randomUUID());

        assertThat(svc.isAccessTokenValid(token)).isFalse();
    }
}
