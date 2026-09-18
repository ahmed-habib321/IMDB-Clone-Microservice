package org.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.util.List;

@Configuration
public class SecurityRulesConfig {

    @Bean
    public List<SecurityRule> securityRules() {
        return List.of(
            new SecurityRule(List.of(
                "/eureka/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/actuator/**",
                "/aggregate/*/v3/api-docs"
            ), SecurityRule.Access.PERMIT_ALL, null),

            new SecurityRule(List.of("/api/v1/auth/**"), SecurityRule.Access.PERMIT_ALL, null),

            new SecurityRule(List.of("/api/v1/private/**"), SecurityRule.Access.DENY_ALL, null),

            // public read of profiles/preferences must be registered BEFORE the /api/v1/user/** catch-all
            new SecurityRule(List.of(
                "/api/v1/user/*/profile",
                "/api/v1/user/*/preferences"
            ), HttpMethod.GET, SecurityRule.Access.PERMIT_ALL, null),

            new SecurityRule(List.of("/api/v1/user/**"), SecurityRule.Access.AUTHENTICATED, null),

            new SecurityRule(List.of("/api/v1/title/**"), HttpMethod.GET, SecurityRule.Access.PERMIT_ALL, null),

            // title-service's internal lookup endpoint is also called by
            // people-service and search-service (no JWT on service-to-service calls).
            new SecurityRule(List.of("/api/v1/title/search"), HttpMethod.POST, SecurityRule.Access.PERMIT_ALL, null),

            new SecurityRule(List.of("/api/v1/title/**"), HttpMethod.POST, SecurityRule.Access.HAS_ANY_ROLE, List.of("ADMIN", "EDITOR")),
            new SecurityRule(List.of("/api/v1/title/**"), HttpMethod.PUT, SecurityRule.Access.HAS_ANY_ROLE, List.of("ADMIN", "EDITOR")),
            new SecurityRule(List.of("/api/v1/title/**"), HttpMethod.DELETE, SecurityRule.Access.HAS_ANY_ROLE, List.of("ADMIN", "EDITOR")),

            new SecurityRule(List.of(
                "/api/v1/people/**",
                "/api/v1/genres/**",
                "/api/v1/search/**",
                "/api/v1/news/**",
                "/api/v1/awards/**",
                "/api/v1/box-office/top",
                "/api/v1/cast/**",
                "/api/v1/crew/**",
                "/api/v1/recommendations/similar/**",
                "/media/**"
            ), HttpMethod.GET, SecurityRule.Access.PERMIT_ALL, null),

            // ratings-reviews-service — public aggregate views for a title
            new SecurityRule(List.of("/api/v1/titles/**"), HttpMethod.GET, SecurityRule.Access.PERMIT_ALL, null),

            // media-service — authorized editors/admin manage images/trailers/box office
            new SecurityRule(List.of(
                "/api/v1/titles/*/images/**",
                "/api/v1/titles/*/trailers/**",
                "/api/v1/titles/*/box-office/**",
                "/api/v1/people/*/images/**"
            ), HttpMethod.POST, SecurityRule.Access.HAS_ANY_ROLE, List.of("ADMIN", "EDITOR")),
            new SecurityRule(List.of(
                "/api/v1/titles/*/box-office/**"
            ), HttpMethod.PUT, SecurityRule.Access.HAS_ANY_ROLE, List.of("ADMIN", "EDITOR")),

            new SecurityRule(List.of(
                "/api/v1/users/me/**",
                "/api/v1/users/**",
                "/api/v1/watchlist/**",
                "/api/v1/lists/**",
                "/api/v1/recommendations/for-me",
                "/api/v1/notification/**"
            ), SecurityRule.Access.AUTHENTICATED, null),

            new SecurityRule(List.of("/api/v1/titles/*/ratings"), HttpMethod.POST, SecurityRule.Access.HAS_ROLE, List.of("USER")),
            new SecurityRule(List.of("/api/v1/titles/*/ratings"), HttpMethod.DELETE, SecurityRule.Access.HAS_ROLE, List.of("USER")),
            new SecurityRule(List.of("/api/v1/titles/*/reviews"), HttpMethod.POST, SecurityRule.Access.HAS_ROLE, List.of("USER")),
            new SecurityRule(List.of("/api/v1/titles/*/trivia"), HttpMethod.POST, SecurityRule.Access.HAS_ROLE, List.of("USER")),
            new SecurityRule(List.of("/api/v1/titles/*/goofs"), HttpMethod.POST, SecurityRule.Access.HAS_ROLE, List.of("USER")),
            new SecurityRule(List.of("/api/v1/titles/*/quotes"), HttpMethod.POST, SecurityRule.Access.HAS_ROLE, List.of("USER")),
            new SecurityRule(List.of("/api/v1/reviews/*/helpfulness"), HttpMethod.POST, SecurityRule.Access.HAS_ROLE, List.of("USER")),
            new SecurityRule(List.of("/api/v1/reviews/*/approve"), HttpMethod.PUT, SecurityRule.Access.HAS_ANY_ROLE, List.of("ADMIN", "EDITOR")),

            // contribution-service — moderation: approve (ADMIN/EDITOR), delete (ADMIN)
            new SecurityRule(List.of(
                "/api/v1/titles/*/trivia/**",
                "/api/v1/titles/*/goofs/**"
            ), HttpMethod.PUT, SecurityRule.Access.HAS_ANY_ROLE, List.of("ADMIN", "EDITOR")),
            new SecurityRule(List.of(
                "/api/v1/titles/*/quotes/**"
            ), HttpMethod.PATCH, SecurityRule.Access.HAS_ANY_ROLE, List.of("ADMIN", "EDITOR")),
            new SecurityRule(List.of(
                "/api/v1/titles/*/trivia/**",
                "/api/v1/titles/*/goofs/**",
                "/api/v1/titles/*/quotes/**"
            ), HttpMethod.DELETE, SecurityRule.Access.HAS_ROLE, List.of("ADMIN")),

            new SecurityRule(List.of(
                "/api/v1/people/**",
                "/api/v1/news/**",
                "/api/v1/awards/**"
            ), HttpMethod.POST, SecurityRule.Access.HAS_ANY_ROLE, List.of("ADMIN", "EDITOR")),
            new SecurityRule(List.of(
                "/api/v1/cast/**",
                "/api/v1/crew/**"
            ), HttpMethod.POST, SecurityRule.Access.HAS_ANY_ROLE, List.of("ADMIN", "EDITOR")),
            new SecurityRule(List.of(
                "/api/v1/people/**",
                "/api/v1/news/**"
            ), HttpMethod.PUT, SecurityRule.Access.HAS_ANY_ROLE, List.of("ADMIN", "EDITOR")),
            new SecurityRule(List.of(
                "/api/v1/news/**"
            ), HttpMethod.DELETE, SecurityRule.Access.HAS_ANY_ROLE, List.of("ADMIN", "EDITOR")),

            new SecurityRule(List.of("/api/v1/admin/**"), SecurityRule.Access.HAS_ROLE, List.of("ADMIN")),
            new SecurityRule(List.of("/api/v1/genres/**"), HttpMethod.POST, SecurityRule.Access.HAS_ROLE, List.of("ADMIN"))
        );
    }
}
