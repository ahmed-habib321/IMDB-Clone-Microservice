package org.example.notificationservice.dto;

import org.example.notificationservice.enums.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
    UUID id,
    String title,
    String message,
    NotificationType type,
    boolean isRead,
    Instant createdAt
) {
}
