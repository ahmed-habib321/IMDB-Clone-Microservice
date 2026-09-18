package org.example.ratingsreviewsservice.repository;

import org.example.ratingsreviewsservice.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findByTitleIdAndIsApprovedTrue(UUID titleId, Pageable pageable);

    boolean existsByUserIdAndTitleId(UUID userId, UUID titleId);

    Page<Review> findByUserId(UUID userId, Pageable pageable);

    @Modifying
    @Query("UPDATE Review r SET r.helpfulYes = :yes, r.helpfulNo = :no WHERE r.id = :reviewId")
    void updateHelpfulness(@Param("reviewId") UUID reviewId,
                           @Param("yes") int yes,
                           @Param("no") int no);
}