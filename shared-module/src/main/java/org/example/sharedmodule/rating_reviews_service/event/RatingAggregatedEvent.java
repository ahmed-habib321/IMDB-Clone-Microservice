package org.example.sharedmodule.rating_reviews_service.event;

public record RatingAggregatedEvent(
        String titleId,
        Double newRating,
        Integer newVoteCount
) {}
