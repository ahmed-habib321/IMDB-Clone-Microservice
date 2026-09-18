package org.example.apigateway.config.Beans.Filters.RateLimit;

import org.example.apigateway.metrics.GatewayMetrics;
import org.example.sharedmodule.general_exceptions.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RateLimitFilterTest {

    @Mock GatewayMetrics gatewayMetrics;
    @Mock ObjectMapper objectMapper;

    RateLimitFilter filter;

    @BeforeEach
    public void setup() throws Exception {
        when(objectMapper.writeValueAsString(any(ErrorResponse.class)))
                .thenReturn("{\"status\":429}");

        RateLimitProperties props = new RateLimitProperties();
        props.setPaths(List.of("/api/v1/auth/**"));
        props.setCapacity(20);
        props.setRefill(20);
        props.setRefillPeriod(Duration.ofMinutes(1));
        props.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:3000"));

        filter = new RateLimitFilter(objectMapper, props, gatewayMetrics);
    }

    private MockHttpServletResponse hit(String ip) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr(ip);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }

    @Test
    void shouldPassThrough_forNonAuthEndpoint() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/titles/123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        verify(gatewayMetrics, never()).recordRateLimitExceeded();
    }

    @Test
    void shouldAllow_firstRequest_toAuthEndpoint() throws Exception {
        hit("10.0.0.1");
        verify(gatewayMetrics, never()).recordRateLimitExceeded();
    }

    @Test
    void shouldIncludeRateLimitHeaders_whenMatched() throws Exception {
        MockHttpServletResponse response = hit("10.0.0.1");

        assertEquals("20", response.getHeader("X-RateLimit-Limit"));
        assertNotNull(response.getHeader("X-RateLimit-Remaining"));
        assertNotNull(response.getHeader("X-RateLimit-Reset"));
    }

    @Test
    void shouldBlock_afterExceedingBucketCapacity() throws Exception {
        for (int i = 0; i < 20; i++) {
            hit("10.0.0.99");
        }

        MockHttpServletResponse blocked = hit("10.0.0.99");

        assertEquals(429, blocked.getStatus());
        verify(gatewayMetrics, times(1)).recordRateLimitExceeded();
    }

    @Test
    void shouldNotBlock_requestsFromDifferentIPs() throws Exception {
        for (int i = 0; i < 25; i++) {
            hit("192.168.1.1");
        }

        MockHttpServletResponse allowed = hit("192.168.1.2");

        assertEquals(200, allowed.getStatus());
        verify(gatewayMetrics, times(5)).recordRateLimitExceeded();
    }

    @Test
    void shouldNotRateLimit_swaggerUi() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/swagger-ui/index.html");
        request.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(response.getHeader("X-RateLimit-Limit"));
        assertNotEquals(429, response.getStatus());
    }

    @Test
    void shouldNotRateLimit_apiDocs() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v3/api-docs");
        request.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(response.getHeader("X-RateLimit-Limit"));
        assertNotEquals(429, response.getStatus());
    }

    @Test
    void shouldBypass_whenOriginMatchesAllowedOrigin() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr("10.0.0.1");
        request.addHeader("Origin", "http://localhost:4200");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(response.getHeader("X-RateLimit-Limit"));
        assertNotEquals(429, response.getStatus());
    }

    @Test
    void shouldBypass_whenRefererMatchesAllowedOrigin() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr("10.0.0.1");
        request.addHeader("Referer", "http://localhost:3000/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(response.getHeader("X-RateLimit-Limit"));
    }

    @Test
    void shouldNotBypass_whenOriginDoesNotMatch() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr("10.0.0.1");
        request.addHeader("Origin", "http://NotMyWebsite.com");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals("20", response.getHeader("X-RateLimit-Limit"));
    }

    @Test
    void shouldNotBypass_whenNoOriginOrRefererPresent() throws Exception {
        MockHttpServletResponse response = hit("10.0.0.1");

        assertEquals("20", response.getHeader("X-RateLimit-Limit"));
    }

    @Test
    void shouldLetFrontendRequest_consumeNoTokens() throws Exception {
        MockHttpServletResponse resp = new MockHttpServletResponse();
        for (int i = 0; i < 20; i++) {
            resp = hit("10.0.0.99");
        }
        assertEquals(200, resp.getStatus());
        resp = hit("10.0.0.99");
        assertEquals(429, resp.getStatus());

        MockHttpServletRequest frontendReq = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        frontendReq.setRemoteAddr("10.0.0.99");
        frontendReq.addHeader("Origin", "http://localhost:4200");
        MockHttpServletResponse frontendResp = new MockHttpServletResponse();

        filter.doFilter(frontendReq, frontendResp, new MockFilterChain());

        assertNotEquals(429, frontendResp.getStatus());
        assertNull(frontendResp.getHeader("X-RateLimit-Limit"));
    }

    @Test
    void shouldUseXForwardedForHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.addHeader("X-Forwarded-For", "10.0.0.1, 10.0.0.2");
        request.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals("20", response.getHeader("X-RateLimit-Limit"));
        assertEquals(200, response.getStatus());
    }

    @Test
    void shouldUseXRealIPHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.addHeader("X-Real-IP", "10.0.0.5");
        request.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals("20", response.getHeader("X-RateLimit-Limit"));
    }

    @Test
    void shouldFallbackToRemoteAddr() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr("10.0.0.99");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals("20", response.getHeader("X-RateLimit-Limit"));
    }

    @Test
    void shouldHandleBlankXForwardedFor() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.addHeader("X-Forwarded-For", " ");
        request.setRemoteAddr("10.0.0.99");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals("20", response.getHeader("X-RateLimit-Limit"));
    }

    @Test
    void shouldDifferentiateByIp() throws Exception {
        String ip1 = "10.0.0.201";
        String ip2 = "10.0.0.202";

        MockHttpServletResponse resp = new MockHttpServletResponse();
        for (int i = 0; i < 20; i++) {
            resp = hit(ip1);
        }
        assertEquals(200, resp.getStatus());
        resp = hit(ip1);
        assertEquals(429, resp.getStatus());

        MockHttpServletResponse allowed = hit(ip2);
        assertNotEquals(429, allowed.getStatus());
    }
}
