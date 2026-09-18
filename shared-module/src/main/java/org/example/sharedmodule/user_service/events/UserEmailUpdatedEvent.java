package org.example.sharedmodule.user_service.events;

import java.util.UUID;

public record UserEmailUpdatedEvent(
        UUID userId,
        String newEmail
) {
}
