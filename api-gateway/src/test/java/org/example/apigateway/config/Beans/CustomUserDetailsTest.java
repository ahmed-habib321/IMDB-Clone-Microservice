package org.example.apigateway.config.Beans;

import org.example.apigateway.model.AuthCredential;
import org.example.apigateway.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    private AuthCredential credential(boolean verified) {
        return AuthCredential.builder()
            .userId(UUID.randomUUID())
            .email("user@test.com")
            .passwordHash("hash")
            .role(Role.USER)
            .isActive(true)
            .isVerified(verified)
            .locked(false)
            .build();
    }

    @Test
    void shouldIncludeRoleAndEmailVerifiedAuthorities() {
        CustomUserDetails details = new CustomUserDetails(credential(true));

        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();

        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("ROLE_USER", "USER", "EMAIL_VERIFIED");
    }

    @Test
    void shouldNotIncludeEmailVerifiedWhenNotVerified() {
        CustomUserDetails details = new CustomUserDetails(credential(false));

        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();

        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("ROLE_USER", "USER");
        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
            .doesNotContain("EMAIL_VERIFIED");
    }

    @Test
    void shouldCacheAuthorities() {
        CustomUserDetails details = new CustomUserDetails(credential(true));

        Collection<? extends GrantedAuthority> first = details.getAuthorities();
        Collection<? extends GrantedAuthority> second = details.getAuthorities();

        assertThat(first).isSameAs(second);
    }
}
