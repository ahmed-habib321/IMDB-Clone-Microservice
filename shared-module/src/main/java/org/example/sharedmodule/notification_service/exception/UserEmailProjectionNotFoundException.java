package org.example.sharedmodule.notification_service.exception;

import java.util.UUID;

public class UserEmailProjectionNotFoundException extends RuntimeException {

    public UserEmailProjectionNotFoundException(UUID userId) {
        super("No email projection found for user " + userId);
    }
}