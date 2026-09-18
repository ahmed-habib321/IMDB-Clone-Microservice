package org.example.sharedmodule.rating_reviews_service.event;

import java.time.Instant;
import java.util.UUID;

public record RatingUpdatedEvent(
        UUID ratingId,
        UUID userId,
        UUID titleId,
        Short oldScore,
        Short newScore,
        Instant updatedAt
) {}