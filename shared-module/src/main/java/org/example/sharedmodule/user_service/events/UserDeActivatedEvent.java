package org.example.sharedmodule.user_service.events;

import java.time.Instant;
import java.util.UUID;

public record UserDeActivatedEvent(
        UUID userId,
        Instant deactivatedAt
) {}