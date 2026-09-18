package org.example.ratingsreviewsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.outbox.service.OutboxWriter;
import org.example.ratingsreviewsservice.dto.rating.RatingDistribution;
import org.example.ratingsreviewsservice.dto.rating.RatingRequest;
import org.example.ratingsreviewsservice.dto.rating.RatingResponse;
import org.example.ratingsreviewsservice.dto.rating.RatingStats;
import org.example.ratingsreviewsservice.dto.rating.UserRatingResponse;
import org.example.sharedmodule.Constants.TOPIC_NAMES;
import org.example.sharedmodule.general_exceptions.BusinessException;
import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.example.ratingsreviewsservice.mapper.RatingMapper;
import org.example.ratingsreviewsservice.model.Rating;
import org.example.ratingsreviewsservice.repository.RatingRepository;
import org.example.sharedmodule.rating_reviews_service.event.RatingAggregatedEvent;
import org.example.sharedmodule.rating_reviews_service.event.RatingUpdatedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final OutboxWriter outboxWriter;

    @Transactional
    public RatingResponse addOrUpdateRating(UUID titleId, RatingRequest req, UUID userId) {
        if (req.score() < 1 || req.score() > 10) throw new BusinessException("Score must be between 1 and 10", HttpStatus.UNPROCESSABLE_CONTENT);

        Optional<Rating> optionalRating = ratingRepository.findByUserIdAndTitleId(userId, titleId);

        boolean isNew = optionalRating.isEmpty();

        Rating rating = optionalRating.orElse(ratingMapper.create(userId, titleId));

        Short oldScore = rating.getScore();
        rating.setScore(req.score());
        Rating saved = ratingRepository.save(rating);

        // Recalculate aggregate in this service's own table
        RatingStats stats = ratingRepository.calculateStats(titleId);
        double rounded = roundAverage(stats.average());
        int cnt = stats.count() != null ? stats.count().intValue() : 0;

        // title-service consumes this and updates imdbRating + voteCount
        outboxWriter.save(
                new RatingAggregatedEvent(titleId.toString(), rounded, cnt),
                TOPIC_NAMES.RATING_AGGREGATED,
                titleId.toString());

        if (!isNew) {
            outboxWriter.save(
                    new RatingUpdatedEvent(saved.getId(), userId, titleId, oldScore, saved.getScore(), Instant.now()),
                    TOPIC_NAMES.RATING_UPDATED,
                    titleId.toString());
        }

        return ratingMapper.toResponse(saved, rounded, cnt);
    }

    @Transactional
    public void deleteRating(UUID titleId, UUID userId) {
        Rating rating = ratingRepository.findByUserIdAndTitleId(userId, titleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating", "userId=" + userId + ", titleId=" + titleId));
        ratingRepository.delete(rating);

        RatingStats stats = ratingRepository.calculateStats(titleId);
        double rounded = roundAverage(stats.average());
        int cnt = stats.count() != null ? stats.count().intValue() : 0;

        // Notify title-service of updated aggregate
        outboxWriter.save(
                new RatingAggregatedEvent(titleId.toString(), rounded, cnt),
                TOPIC_NAMES.RATING_AGGREGATED,
                titleId.toString());
    }

    /**
     * Returns the current aggregate stats for a title (imdbRating + voteCount).
     * Calculated live from this service's ratings table.
     */
    @Transactional(readOnly = true)
    public RatingResponse getRatings(UUID titleId) {
        RatingStats stats = ratingRepository.calculateStats(titleId);
        return ratingMapper.toResponse(roundAverage(stats.average()),
                stats.count() != null ? stats.count().intValue() : 0);
    }

    /**
     * Returns a user's rating history.
     * Note: title slug/poster are not available here — the client must
     * enrich the response by calling title-service with the returned titleIds,
     * or the API gateway can aggregate them.
     */
    @Transactional(readOnly = true)
    public Page<UserRatingResponse> getUserRatings(UUID userId, Pageable pageable) {
        return ratingRepository.findByUserId(userId, pageable)
                .map(ratingMapper::toUserRatingResponse);
    }

    @Transactional(readOnly = true)
    public RatingDistribution getDistribution(UUID titleId) {
        Map<Integer, Long> dist = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) dist.put(i, 0L);
        for (Object[] row : ratingRepository.findDistributionRaw(titleId)) {
            dist.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }
        RatingStats stats = ratingRepository.calculateStats(titleId);
        return ratingMapper.toDistribution(dist, roundAverage(stats.average()),
                stats.count() != null ? stats.count() : 0L);
    }

    private double roundAverage(Double avg) {
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }
}