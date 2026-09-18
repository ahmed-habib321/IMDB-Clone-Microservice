package org.example.notificationservice.mapper;

import org.example.notificationservice.dto.NotificationResponse;
import org.example.notificationservice.dto.UnreadCountResponse;
import org.example.notificationservice.entity.EmailJob;
import org.example.notificationservice.entity.Notification;
import org.example.notificationservice.enums.NotificationTemplate;
import org.example.notificationservice.enums.NotificationType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NotificationMapper {

    @Value("${app.brand-name:IMDb}")
    private String brandName;

    public EmailJob emailJob(UUID messageId, String recipient, NotificationTemplate template, String variablesJson) {
        return EmailJob.builder()
                .messageId(messageId)
                .recipient(recipient)
                .template(template)
                .variablesJson(variablesJson)
                .build();
    }

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getType(),
            notification.isRead(),
            notification.getCreatedAt()
        );
    }

    public Notification create(UUID userId, String title, String message, NotificationType type) {
        return Notification.builder()
            .userId(userId)
            .title(title.replace("{brand}", brandName))
            .message(message.replace("{brand}", brandName))
            .type(type)
            .build();
    }

    public UnreadCountResponse toUnreadCountResponse(long count) {
        return new UnreadCountResponse(count);
    }
}
