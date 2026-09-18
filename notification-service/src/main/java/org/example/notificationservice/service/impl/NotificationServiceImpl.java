package org.example.notificationservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.sharedmodule.api_gateway.event.PasswordChangedEvent;
import org.example.notificationservice.dto.NotificationResponse;
import org.example.notificationservice.dto.UnreadCountResponse;
import org.example.notificationservice.entity.Notification;
import org.example.notificationservice.entity.UserEmailProjection;
import org.example.notificationservice.enums.NotificationTemplate;
import org.example.sharedmodule.notification_service.exception.NotificationNotFoundException;
import org.example.sharedmodule.notification_service.exception.UserEmailProjectionNotFoundException;
import org.example.notificationservice.mapper.NotificationMapper;
import org.example.notificationservice.repository.NotificationRepository;
import org.example.notificationservice.repository.UserEmailProjectionRepository;
import org.example.notificationservice.service.EmailJobService;
import org.example.notificationservice.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailJobService emailJobService;
    private final NotificationRepository notificationRepository;
    private final UserEmailProjectionRepository userEmailProjectionRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void handlePasswordChanged(PasswordChangedEvent event) {
        UserEmailProjection user = userEmailProjectionRepository
            .findById(event.userId())
            .orElseThrow(() -> new UserEmailProjectionNotFoundException(event.userId()));

        NotificationTemplate template = NotificationTemplate.PASSWORD_CHANGED;

        notificationRepository.save(
            notificationMapper.create(
                event.userId(),
                template.getInAppTitle(),
                template.getInAppMessage(),
                template.getNotificationType()
            )
        );

        emailJobService.createEmailJob(
            event.messageId(),
            user.getEmail(),
            template,
            Map.of()
        );
    }

    @Override
    public List<NotificationResponse> getNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(notificationMapper::toResponse)
            .toList();
    }

    @Override
    public UnreadCountResponse getUnreadCount(UUID userId) {
        long count = notificationRepository.countByUserIdAndIsReadFalse(userId);
        return notificationMapper.toUnreadCountResponse(count);
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository
            .findByIdAndUserId(notificationId, userId)
            .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        if (!notification.isRead()) {
            notification.setRead(true);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsRead(userId);
    }
}
