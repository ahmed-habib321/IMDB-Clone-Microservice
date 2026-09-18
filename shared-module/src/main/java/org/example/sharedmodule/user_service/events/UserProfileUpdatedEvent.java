package org.example.sharedmodule.user_service.events;

import java.time.Instant;
import java.util.UUID;

public record UserProfileUpdatedEvent(
        UUID userId,
        String displayName,
        String avatarUrl,
        Instant updatedAt
) {}