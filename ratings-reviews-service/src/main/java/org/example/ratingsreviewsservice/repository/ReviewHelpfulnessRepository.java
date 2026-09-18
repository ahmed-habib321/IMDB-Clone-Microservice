package org.example.ratingsreviewsservice.repository;

import org.example.ratingsreviewsservice.model.ReviewHelpfulness;
import org.example.ratingsreviewsservice.model.ReviewHelpfulnessId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewHelpfulnessRepository
        extends JpaRepository<ReviewHelpfulness, ReviewHelpfulnessId> {

    @Query("SELECT h FROM ReviewHelpfulness h WHERE h.id.userId = :userId AND h.id.reviewId = :reviewId")
    Optional<ReviewHelpfulness> findByUserIdAndReviewId(@Param("userId") UUID userId, @Param("reviewId") UUID reviewId);

    @Query("SELECT COUNT(h) FROM ReviewHelpfulness h WHERE h.id.reviewId = :reviewId AND h.isHelpful = :helpful")
    int countByReviewIdAndHelpful(@Param("reviewId") UUID reviewId, @Param("helpful") boolean helpful);
}