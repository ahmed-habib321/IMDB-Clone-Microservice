package org.example.ratingsreviewsservice.dto.rating;

import java.util.UUID;

public record RatingResponse(
        UUID id,
        Short userScore,        // the score this user gave (null if querying aggregate)
        Double averageRating,
        Integer voteCount
) {}
