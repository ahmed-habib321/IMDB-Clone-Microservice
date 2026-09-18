package org.example.apigateway.config.Beans.Filters.RateLimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.sharedmodule.general_exceptions.ErrorResponse;
import org.example.apigateway.metrics.GatewayMetrics;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
@Slf4j
@EnableConfigurationProperties(RateLimitProperties.class)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final List<PathPatternRequestMatcher> NEVER_RATE_LIMITED = List.of(
            PathPatternRequestMatcher.pathPattern("/swagger-ui.html"),
            PathPatternRequestMatcher.pathPattern("/swagger-ui/**"),
            PathPatternRequestMatcher.pathPattern("/v3/api-docs/**"),
            PathPatternRequestMatcher.pathPattern("/aggregate/*/v3/api-docs")
    );

    private final ObjectMapper objectMapper;
    private final RateLimitProperties rateLimitProperties;
    private final GatewayMetrics gatewayMetrics;
    private final List<PathPatternRequestMatcher> pathMatchers;

    public RateLimitFilter(ObjectMapper objectMapper, RateLimitProperties rateLimitProperties, GatewayMetrics gatewayMetrics) {
        this.objectMapper = objectMapper;
        this.rateLimitProperties = rateLimitProperties;
        this.gatewayMetrics = gatewayMetrics;
        this.pathMatchers = rateLimitProperties.getPaths().stream()
                .map(PathPatternRequestMatcher::pathPattern)
                .toList();
    }

    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(2))
            .maximumSize(100_000)
            .build();

    private String resolveClientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        String xri = req.getHeader("X-Real-IP");
        if (xri != null && !xri.isBlank()) {
            return xri;
        }
        return req.getRemoteAddr();
    }

    private boolean isFromFrontend(HttpServletRequest req) {
        List<String> allowedOrigins = rateLimitProperties.getAllowedOrigins();
        if (allowedOrigins == null || allowedOrigins.isEmpty()) {
            return false;
        }
        String origin = req.getHeader("Origin");
        if (origin != null && allowedOrigins.contains(origin)) {
            return true;
        }
        String referer = req.getHeader("Referer");
        if (referer != null) {
            try {
                java.net.URI uri = new java.net.URI(referer);
                String refererOrigin = uri.getScheme() + "://" + uri.getAuthority();
                if (allowedOrigins.contains(refererOrigin)) {
                    return true;
                }
            } catch (Exception ignored) {}
        }
        return false;
    }

    private Bucket bucket(String key) {
        return buckets.get(key, k -> Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(rateLimitProperties.getCapacity())
                        .refillIntervally(rateLimitProperties.getRefill(), rateLimitProperties.getRefillPeriod())
                        .build())
                .build());
    }

    private void writeRateLimitHeaders(HttpServletResponse res, Bucket bucket) {
        long available = bucket.getAvailableTokens();
        res.setHeader("X-RateLimit-Limit", String.valueOf(rateLimitProperties.getCapacity()));
        res.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, available - 1)));
        res.setHeader("X-RateLimit-Reset", String.valueOf(
                Instant.now().plus(rateLimitProperties.getRefillPeriod()).getEpochSecond()));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (isFromFrontend(request)) {
            return true;
        }
        if (NEVER_RATE_LIMITED.stream().anyMatch(m -> m.matches(request))) {
            return true;
        }
        return pathMatchers.stream().noneMatch(m -> m.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String ip = resolveClientIp(req);
        Bucket bucket = bucket(ip);

        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for IP: {}", ip);
            gatewayMetrics.recordRateLimitExceeded();
            writeRateLimitHeaders(res, bucket);
            ErrorResponse body = ErrorResponse.of(429, "Too Many Requests", "Rate limit exceeded. Retry after " + rateLimitProperties.getRefillPeriod().toSeconds() + " seconds");
            res.setStatus(429);
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.getWriter().write(objectMapper.writeValueAsString(body));
            return;
        }

        writeRateLimitHeaders(res, bucket);
        chain.doFilter(req, res);
    }
}
