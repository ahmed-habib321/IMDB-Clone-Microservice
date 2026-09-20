package org.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SecurityRulesConfig {

    @Bean
    public List<SecurityRule> securityRules() {
        return List.of(
            // TEMPORARY: every endpoint is open for development.
            new SecurityRule(List.of("/**"), SecurityRule.Access.PERMIT_ALL, null)
        );
    }
}
