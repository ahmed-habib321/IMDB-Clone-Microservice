package org.example.outbox.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.example.outbox.relay.OutboxProperties;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(OutboxProperties.class)
public class OutboxJpaConfiguration {

    @Bean
    HibernatePropertiesCustomizer outboxShowSqlDisabler() {
        return properties -> properties.put("hibernate.show_sql", false);
    }
}