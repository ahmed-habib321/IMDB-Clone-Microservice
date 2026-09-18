package org.example.ratingsreviewsservice.mapper;


import org.example.ratingsreviewsservice.dto.review.ReviewRequest;
import org.example.ratingsreviewsservice.dto.review.ReviewResponse;
import org.example.ratingsreviewsservice.model.Review;
import org.example.ratingsreviewsservice.model.ReviewHelpfulness;
import org.example.ratingsreviewsservice.model.ReviewHelpfulnessId;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    default Review create(UUID userId, UUID titleId, ReviewRequest req) {
        return Review.builder()
                .userId(userId)
                .titleId(titleId)
                .reviewTitle(req.reviewTitle())
                .body(req.body())
                .containsSpoiler(req.containsSpoiler())
                .isApproved(false)
                .helpfulYes(0)
                .helpfulNo(0)
                .build();
    }

    default ReviewHelpfulness helpfulness(UUID userId, UUID reviewId) {
        return ReviewHelpfulness.builder()
                .id(new ReviewHelpfulnessId(userId, reviewId))
                .review(Review.builder().id(reviewId).build())
                .build();
    }

    ReviewResponse toResponse(Review review);
}