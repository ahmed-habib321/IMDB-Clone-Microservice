package org.example.notificationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static org.example.sharedmodule.Constants.TOPIC_NAMES.*;

@Configuration
public class KafkaTopicConfig {

    private static final int PARTITIONS = 6;
    private static final short REPLICAS = 1;

    private NewTopic createTopic(String name) {
        return TopicBuilder.name(name)
            .partitions(PARTITIONS)
            .replicas(REPLICAS)
            .build();
    }

    @Bean
    public NewTopic emailVerificationTopic() {
        return createTopic(AUTH_EMAIL_VERIFICATION);
    }

    @Bean
    public NewTopic passwordResetTopic() {
        return createTopic(AUTH_PASSWORD_RESET);
    }

    @Bean
    public NewTopic passwordChangedTopic() {
        return createTopic(AUTH_PASSWORD_CHANGE);
    }

    @Bean
    public NewTopic userDeactivatedTopic() {
        return createTopic(USER_DEACTIVATED);
    }

    @Bean
    public NewTopic userProfileUpdatedTopic() {
        return createTopic(USER_PROFILE_UPDATED);
    }

    @Bean
    public NewTopic userEmailUpdatedTopic() {
        return createTopic(USER_EMAIL_UPDATED);
    }
}
