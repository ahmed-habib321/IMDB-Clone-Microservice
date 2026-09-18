package org.example.ratingsreviewsservice.mapper;

import org.example.ratingsreviewsservice.dto.rating.RatingDistribution;
import org.example.ratingsreviewsservice.dto.rating.RatingResponse;
import org.example.ratingsreviewsservice.dto.rating.UserRatingResponse;
import org.example.ratingsreviewsservice.model.Rating;
import org.mapstruct.Mapper;

import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    default Rating create(UUID userId, UUID titleId) {
        return Rating.builder()
                .userId(userId)
                .titleId(titleId)
                .build();
    }

    default RatingResponse toResponse(Rating rating, double average, long count) {
        return new RatingResponse(rating.getId(), rating.getScore(), average, (int) count);
    }

    default RatingResponse toResponse(double average, long count) {
        return new RatingResponse(null, null, average, (int) count);
    }

    default UserRatingResponse toUserRatingResponse(Rating rating) {
        return new UserRatingResponse(rating.getTitleId(), rating.getScore(), rating.getRatedAt());
    }

    default RatingDistribution toDistribution(Map<Integer, Long> distribution, double average, long count) {
        return new RatingDistribution(distribution, average, count);
    }
}