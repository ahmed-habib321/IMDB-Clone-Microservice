package org.example.notificationservice.service;

import org.example.sharedmodule.api_gateway.event.PasswordChangedEvent;
import org.example.notificationservice.dto.NotificationResponse;
import org.example.notificationservice.dto.UnreadCountResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    void handlePasswordChanged(PasswordChangedEvent event);

    List<NotificationResponse> getNotifications(UUID userId);

    UnreadCountResponse getUnreadCount(UUID userId);

    void markAsRead(UUID notificationId, UUID userId);

    void markAllAsRead(UUID userId);
}
