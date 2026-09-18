package org.example.ratingsreviewsservice.dto.review;

import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID userId,
        String reviewTitle,
        String body,
        boolean containsSpoiler,
        boolean isApproved,
        Integer helpfulYes,
        Integer helpfulNo,
        Instant createdAt
) {}
