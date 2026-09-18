package org.example.apigateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sharedmodule.general_exceptions.BusinessException;
import org.example.apigateway.model.RefreshToken;
import org.example.apigateway.model.TokenType;
import org.example.apigateway.repository.AccessTokenRepository;
import org.example.apigateway.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final StringRedisTemplate redis;

    private static final String OTP_KEY_PREFIX = "auth:otp:";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access-token-expiry-ms}")
    private long accessExpiry;
    @Value("${jwt.refresh-token-expiry-ms}")
    private long refreshExpiry;
    @Value("${jwt.email-token-expiry-ms}")
    private long emailExpiry;
    @Value("${jwt.password-reset-expiry-ms}")
    private long passwordResetExpiry;
    @Value("${jwt.issuer}")
    private String issuer;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("jwt.secret must be at least 32 bytes (256 bits) for HMAC-SHA256");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // ── Access token ──────────────────────────────────────────────────────────

    public String generateAccessToken(UUID userId, String email, List<String> roles, UUID sessionId) {
        String jti = UUID.randomUUID().toString();

        List<String> rawRoles = roles.stream()
            .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
            .collect(Collectors.toList());

        String token = buildAccessToken(userId.toString(), jti, email, rawRoles, sessionId);
        accessTokenRepository.insertToRedis(userId.toString(), jti, Duration.ofMillis(accessExpiry));
        return token;
    }

    private String buildAccessToken(String userId, String jti, String email, List<String> roles, UUID sessionId) {
        return Jwts.builder()
            .issuer(issuer)
            .subject(userId)
            .id(jti)
            .claim("email", email)
            .claim("roles", roles)
            .claim("type", TokenType.ACCESS.name())
            .claim("sid", sessionId.toString())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + accessExpiry))
            .signWith(secretKey)
            .compact();
    }

    public boolean isAccessTokenValid(String token) {
        try {
            Claims claims = parseToken(token);
            if (!TokenType.ACCESS.name().equals(claims.get("type"))) return false;
            return accessTokenRepository.existsInRedis(claims.getId());
        } catch (Exception e) {
            return false;
        }
    }

    // ── Refresh token ─────────────────────────────────────────────────────────

    public RefreshToken createNewRefreshToken(UUID userId) {
        RefreshToken session = RefreshToken.builder()
            .sessionId(UUID.randomUUID())
            .userId(userId)
            .revoked(false)
            .expiryDate(Instant.now().plusMillis(refreshExpiry))
            .build();
        return refreshTokenRepository.save(session);
    }

    public String generateRefreshToken(UUID userId, UUID sessionId) {
        return Jwts.builder()
            .subject(userId.toString())
            .claim("sid", sessionId.toString())
            .claim("type", TokenType.REFRESH.name())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + refreshExpiry))
            .signWith(secretKey)
            .compact();
    }

    @Transactional
    public RefreshToken refresh(String refreshToken) {
        Claims claims = parseToken(refreshToken);
        assertTokenType(claims, TokenType.REFRESH);

        UUID oldSessionId = UUID.fromString(claims.get("sid", String.class));
        RefreshToken oldSession = refreshTokenRepository.findRefreshTokenBySessionId(oldSessionId)
            .orElseThrow(() -> new BusinessException("Invalid refresh token, please log in again", HttpStatus.UNAUTHORIZED));

        if (oldSession.isRevoked()) {
            UUID userId = oldSession.getUserId();
            refreshTokenRepository.deleteByUserId(userId);
            accessTokenRepository.removeAllActiveSessionsFromRedis(userId);
            throw new BusinessException("Refresh token reuse detected — all sessions invalidated", HttpStatus.UNAUTHORIZED);
        }

        if (oldSession.getExpiryDate().isBefore(Instant.now())) {
            throw new BusinessException("Refresh token expired, please log in again", HttpStatus.UNAUTHORIZED);
        }

        oldSession.setRevoked(true);
        refreshTokenRepository.save(oldSession);

        RefreshToken newSession = RefreshToken.builder()
            .sessionId(UUID.randomUUID())
            .userId(oldSession.getUserId())
            .revoked(false)
            .expiryDate(Instant.now().plusMillis(refreshExpiry))
            .build();

        return refreshTokenRepository.save(newSession);
    }

    // ── Session management ────────────────────────────────────────────────────

    @Transactional
    public void invalidateSession(String accessToken) {
        Claims claims = parseToken(accessToken);
        assertTokenType(claims, TokenType.ACCESS);

        UUID sessionId = UUID.fromString(claims.get("sid", String.class));
        refreshTokenRepository.deleteBySessionId(sessionId);
        accessTokenRepository.removeSessionFromRedis(claims);
    }

    @Transactional
    public void invalidateAllSession(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
        accessTokenRepository.removeAllActiveSessionsFromRedis(userId);
    }

    // ── Email verification OTP ─────────────────────────────────────────────

    public String generateEmailVerificationOtp(UUID userId) {
        String otp = generateOtp();
        String key = otpKey("EMAIL", otp);
        redis.opsForValue().set(key, userId.toString(), Duration.ofMillis(emailExpiry));
        return otp;
    }

    public UUID validateEmailOtp(String otp) {
        String key = otpKey("EMAIL", otp);
        String userId = redis.opsForValue().get(key);
        if (userId == null) {
            throw new BusinessException("Invalid or expired email verification OTP", HttpStatus.BAD_REQUEST);
        }
        redis.delete(key);
        return UUID.fromString(userId);
    }

    // ── Password reset OTP ─────────────────────────────────────────────────

    public String generatePasswordResetOtp(UUID userId) {
        String otp = generateOtp();
        String key = otpKey("PASSWORD", otp);
        redis.opsForValue().set(key, userId.toString(), Duration.ofMillis(passwordResetExpiry));
        return otp;
    }

    public UUID validatePasswordResetOtp(String otp) {
        String key = otpKey("PASSWORD", otp);
        String userId = redis.opsForValue().get(key);
        if (userId == null) {
            throw new BusinessException("Invalid or expired password reset OTP", HttpStatus.BAD_REQUEST);
        }
        redis.delete(key);
        return UUID.fromString(userId);
    }

    // ── Shared helpers ────────────────────────────────────────────────────────

    public Claims parseToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private void assertTokenType(Claims claims, TokenType expected) {
        if (!expected.name().equals(claims.get("type")))
            throw new BusinessException("Invalid token type", HttpStatus.UNAUTHORIZED);
    }

    private String generateOtp() {
        int otp = SECURE_RANDOM.nextInt(1_000_000);
        return String.format("%06d", otp);
    }

    private String otpKey(String type, String otp) {
        return OTP_KEY_PREFIX + type + ":" + otp;
    }
}