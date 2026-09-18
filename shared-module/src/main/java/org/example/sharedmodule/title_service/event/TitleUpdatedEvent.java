package org.example.sharedmodule.title_service.event;

import java.time.Instant;
import java.util.UUID;

public record TitleUpdatedEvent(
        UUID titleId,
        String slug,
        Instant updatedAt
) {}