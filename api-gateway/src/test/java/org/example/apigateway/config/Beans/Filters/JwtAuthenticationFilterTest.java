package org.example.apigateway.config.Beans.Filters;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.apigateway.service.AuthService;
import org.example.apigateway.service.AuthTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock AuthTokenService authTokenService;
    @Mock AuthService authService;
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock FilterChain filterChain;
    @Mock Claims claims;

    @InjectMocks JwtAuthenticationFilter filter;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("when go through the filter with no Auth it shouldn't stop you and won't create an Authentication for you")
    void shouldPassThrough_whenNoAuthHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("when go through the filter with wrong Header will not stop you and won't create an Authentication for you")
    void shouldPassThrough_whenHeaderNotBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic somebase64");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("when go through the filter with valid token will create an Authentication for you")
    void shouldAuthenticateUser_whenValidToken() throws Exception {
        UUID userId = UUID.randomUUID();

        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
        when(authTokenService.isAccessTokenValid("valid.token.here")).thenReturn(true);
        when(authTokenService.parseToken("valid.token.here")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(userId.toString());
        when(claims.get("roles")).thenReturn(List.of("USER"));
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo(userId.toString());
    }


    @Test
    @DisplayName("when go through the filter with Authentication will not process you again")
    void shouldNotProcess_whenPresentAuthentication() throws Exception {
        UUID userId = UUID.randomUUID();
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
        when(authTokenService.isAccessTokenValid("valid.token.here")).thenReturn(true);
        when(authTokenService.parseToken("valid.token.here")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(userId.toString());
        when(claims.get("roles")).thenReturn(List.of("USER"));
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        filter.doFilterInternal(request, response, filterChain);
        filter.doFilterInternal(request, response, filterChain);
        filter.doFilterInternal(request, response, filterChain);

        verify(authTokenService,times(1)).parseToken(anyString());
    }

    @Test
    @DisplayName("when go through the filter with invalid token will not create Authentication for you")
    void shouldSkipAuthentication_whenTokenInvalid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer bad.token");
        when(authTokenService.isAccessTokenValid("bad.token")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("when go through the filter with token that cause an error will not create Authentication for you")
    void shouldContinueAsAnonymous_whenTokenThrowsException() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer corrupt.token");
        when(authTokenService.isAccessTokenValid("corrupt.token")).thenThrow(new RuntimeException("parse error"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("when token has null roles it authenticates with empty authorities")
    void shouldAuthenticate_whenRolesNull() throws Exception {
        UUID userId = UUID.randomUUID();
        when(request.getHeader("Authorization")).thenReturn("Bearer null-roles.token");
        when(authTokenService.isAccessTokenValid("null-roles.token")).thenReturn(true);
        when(authTokenService.parseToken("null-roles.token")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(userId.toString());
        when(claims.get("roles")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("when roles already carry the ROLE_ prefix they are kept as-is")
    void shouldAuthenticate_whenRolesPrefixed() throws Exception {
        UUID userId = UUID.randomUUID();
        when(request.getHeader("Authorization")).thenReturn("Bearer prefixed.token");
        when(authTokenService.isAccessTokenValid("prefixed.token")).thenReturn(true);
        when(authTokenService.parseToken("prefixed.token")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(userId.toString());
        when(claims.get("roles")).thenReturn(List.of("ROLE_ADMIN"));
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getAuthorities())
            .extracting("authority")
            .containsExactly("ROLE_ADMIN");
    }

    // ── Empty / refresh-header scenarios (from good-code) ───────────────────

    @Test
    @DisplayName("when the access token header is blank it is skipped")
    void shouldPassThrough_whenAccessTokenEmpty() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("");
        when(request.getHeader("X-Refresh-Token")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("valid access token authenticates even when a refresh header is present")
    void shouldAuthenticate_whenValidAccessWithRefreshHeader() throws Exception {
        UUID userId = UUID.randomUUID();
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-access");
        when(request.getHeader("X-Refresh-Token")).thenReturn("any-refresh");
        when(authTokenService.isAccessTokenValid("valid-access")).thenReturn(true);
        when(authTokenService.parseToken("valid-access")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(userId.toString());
        when(claims.get("roles")).thenReturn(List.of("USER"));
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        verify(authService, never()).refresh(anyString());
    }

    @Test
    @DisplayName("stays anonymous when both access and refresh tokens are invalid")
    void shouldStayAnonymous_whenBothTokensInvalid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("expired-access");
        when(request.getHeader("X-Refresh-Token")).thenReturn("expired-refresh");
        when(authTokenService.isAccessTokenValid("expired-access")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(authService).refresh("expired-refresh");
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(response, never()).setHeader(anyString(), anyString());
    }

    @Test
    @DisplayName("stays anonymous when refresh attempt fails with only a refresh token")
    void shouldDoNothing_whenOnlyRefreshToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-Refresh-Token")).thenReturn("valid-refresh");

        filter.doFilterInternal(request, response, filterChain);

        verify(authService).refresh("valid-refresh");
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(response, never()).setHeader(anyString(), anyString());
    }
}
