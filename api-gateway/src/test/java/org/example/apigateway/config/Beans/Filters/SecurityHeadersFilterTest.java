package org.example.apigateway.config.Beans.Filters;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SecurityHeadersFilterTest {

    private final SecurityHeadersFilter filter = new SecurityHeadersFilter();

    @Test
    void shouldPutHeaders_whenRequestComes() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        filter.doFilterInternal(request, response, filterChain);

        assertEquals("nosniff",
                response.getHeader("X-Content-Type-Options"));

        assertEquals("SAMEORIGIN",
                response.getHeader("X-Frame-Options"));

        assertEquals("strict-origin-when-cross-origin",
                response.getHeader("Referrer-Policy"));

        assertEquals("max-age=31536000; includeSubDomains",
                response.getHeader("Strict-Transport-Security"));

        assertEquals("camera=(), microphone=(), geolocation=()",
                response.getHeader("Permissions-Policy"));

        assertEquals(
                "default-src 'self'; " +
                        "img-src 'self' data: https:; " +
                        "script-src 'self' 'unsafe-inline'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "frame-ancestors 'self'; " +
                        "frame-src https://www.youtube.com;",
                response.getHeader("Content-Security-Policy")
        );

        verify(filterChain).doFilter(request, response);
    }

}
