package org.example.ratingsreviewsservice.repository;

import org.example.ratingsreviewsservice.dto.rating.RatingStats;
import org.example.ratingsreviewsservice.model.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    Optional<Rating> findByUserIdAndTitleId(UUID userId, UUID titleId);
    Page<Rating> findByUserId(UUID userId, Pageable pageable);

    @Query("""
            SELECT new org.example.ratingsreviewsservice.dto.rating.RatingStats(
                AVG(CAST(r.score AS double)), COUNT(r))
            FROM Rating r WHERE r.titleId = :titleId
            """)
    RatingStats calculateStats(@Param("titleId") UUID titleId);

    @Query("""
            SELECT r.score, COUNT(r) FROM Rating r
            WHERE r.titleId = :titleId
            GROUP BY r.score ORDER BY r.score
            """)
    List<Object[]> findDistributionRaw(@Param("titleId") UUID titleId);
}