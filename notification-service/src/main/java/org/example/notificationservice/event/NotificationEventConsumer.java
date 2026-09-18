package org.example.notificationservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.example.sharedmodule.api_gateway.event.EmailVerificationEvent;
import org.example.sharedmodule.api_gateway.event.PasswordChangedEvent;
import org.example.sharedmodule.api_gateway.event.PasswordResetEvent;
import org.example.notificationservice.entity.UserEmailProjection;
import org.example.notificationservice.enums.NotificationTemplate;
import org.example.sharedmodule.notification_service.exception.UserEmailProjectionNotFoundException;
import org.example.notificationservice.repository.UserEmailProjectionRepository;
import org.example.notificationservice.service.EmailJobService;
import org.example.notificationservice.service.NotificationService;
import org.example.sharedmodule.utils.LogUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.example.sharedmodule.Constants.SERVICE_NAMES.NOTIFICATION_SERVICE;
import static org.example.sharedmodule.Constants.TOPIC_NAMES.*;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final EmailJobService emailJobService;
    private final UserEmailProjectionRepository userEmailProjectionRepository;
    private final NotificationService notificationService;

    @KafkaListener(topics = AUTH_EMAIL_VERIFICATION)
    @Transactional
    public void EmailVerificationConsumer(EmailVerificationEvent event) {
        LogUtils.logEventConsumed(NOTIFICATION_SERVICE, AUTH_EMAIL_VERIFICATION);

        if (!userEmailProjectionRepository.existsById(event.userId())) {
            userEmailProjectionRepository.save(new UserEmailProjection(event.userId(), event.email()));
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("verificationToken", event.token());

        emailJobService.createEmailJob(
            event.messageId(),
            event.email(),
            NotificationTemplate.REGISTRATION,
            variables
        );
    }

    @KafkaListener(topics = AUTH_PASSWORD_RESET)
    @Transactional
    public void PasswordResetConsumer(PasswordResetEvent event) {
        LogUtils.logEventConsumed(NOTIFICATION_SERVICE, AUTH_PASSWORD_RESET);

        UserEmailProjection user = userEmailProjectionRepository
            .findById(event.userId())
            .orElseThrow(() -> new UserEmailProjectionNotFoundException(event.userId()));

        Map<String, Object> variables = new HashMap<>();
        variables.put("resetUrl", event.token());

        emailJobService.createEmailJob(
            event.messageId(),
            user.getEmail(),
            NotificationTemplate.PASSWORD_RESET,
            variables
        );
    }

    @KafkaListener(topics = AUTH_PASSWORD_CHANGE)
    @Transactional
    public void PasswordChangedConsumer(PasswordChangedEvent event) {
        LogUtils.logEventConsumed(NOTIFICATION_SERVICE, AUTH_PASSWORD_CHANGE);
        notificationService.handlePasswordChanged(event);
    }
}
