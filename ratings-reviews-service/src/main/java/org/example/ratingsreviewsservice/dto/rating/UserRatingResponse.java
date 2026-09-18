package org.example.ratingsreviewsservice.dto.rating;

import java.time.Instant;
import java.util.UUID;

public record UserRatingResponse(
        UUID titleId,
        Short score,
        Instant ratedAt
) {}
