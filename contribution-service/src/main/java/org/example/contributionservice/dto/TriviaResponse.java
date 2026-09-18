package org.example.contributionservice.dto;


import java.time.Instant;
import java.util.UUID;

public record TriviaResponse(
        UUID id,
        String body,
        boolean isSpoiler,
        boolean isApproved,
        Integer helpfulCount,
        Instant createdAt
) {}
