package org.example.ratingsreviewsservice.dto.rating;

import java.util.Map;

public record RatingDistribution(
        Map<Integer, Long> distribution,    // score 1-10 → count
        Double averageRating,
        Long totalVotes
) {}
