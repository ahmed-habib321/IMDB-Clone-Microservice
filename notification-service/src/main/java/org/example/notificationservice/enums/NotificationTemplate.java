package org.example.notificationservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationTemplate {

    REGISTRATION(
        "Verify your {brand} account",
        "email/registration",
        "Welcome to {brand}",
        "Your {brand} account was created. Please verify your email to get started.",
        NotificationType.REGISTRATION
    ),

    PASSWORD_RESET(
        "Reset your password",
        "email/password-reset",
        "Password reset requested",
        "A request to reset your {brand} password was received.",
        NotificationType.PASSWORD_RESET
    ),

    PASSWORD_CHANGED(
        "Your password has been changed",
        "email/password-changed",
        "Password changed",
        "Your {brand} password was successfully changed.",
        NotificationType.PASSWORD_CHANGED
    );

    private final String emailSubject;
    private final String emailTemplate;
    private final String inAppTitle;
    private final String inAppMessage;
    private final NotificationType notificationType;
}
