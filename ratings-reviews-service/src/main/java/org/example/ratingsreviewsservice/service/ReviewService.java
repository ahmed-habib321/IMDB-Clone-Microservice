package org.example.ratingsreviewsservice.service;

import lombok.RequiredArgsConstructor;
import org.example.ratingsreviewsservice.dto.review.ReviewRequest;
import org.example.ratingsreviewsservice.dto.review.ReviewResponse;
import org.example.ratingsreviewsservice.mapper.ReviewMapper;
import org.example.ratingsreviewsservice.model.Review;
import org.example.ratingsreviewsservice.model.ReviewHelpfulness;
import org.example.ratingsreviewsservice.repository.ReviewHelpfulnessRepository;
import org.example.ratingsreviewsservice.repository.ReviewRepository;
import org.example.sharedmodule.general_exceptions.BusinessException;
import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewHelpfulnessRepository helpfulnessRepository;
    private final ReviewMapper reviewMapper;

    // ────────────────────────────────────────────────
    //  CREATE
    // ────────────────────────────────────────────────
    @Transactional
    public ReviewResponse addReview(UUID userId, UUID titleId, ReviewRequest req) {
        if (reviewRepository.existsByUserIdAndTitleId(userId, titleId)) throw new BusinessException("You have already reviewed this title", HttpStatus.UNPROCESSABLE_CONTENT);

        Review saved = reviewRepository.save(reviewMapper.create(userId, titleId, req));

        return reviewMapper.toResponse(saved);
    }

    // ────────────────────────────────────────────────
    //  READ
    // ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviews(UUID titleId, Pageable pageable) {
        return reviewRepository.findByTitleIdAndIsApprovedTrue(titleId, pageable)
                .map(reviewMapper::toResponse);

    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getUserReviews(UUID userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable)
                .map(reviewMapper::toResponse);
    }

    // ────────────────────────────────────────────────
    //  APPROVE (EDITOR only — enforced at controller level)
    // ────────────────────────────────────────────────

    @Transactional
    public ReviewResponse approveReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));
        review.setIsApproved(true);
        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    // ────────────────────────────────────────────────
    //  HELPFULNESS VOTE
    // ────────────────────────────────────────────────

    @Transactional
    public void voteHelpful(UUID userId, UUID reviewId, boolean isHelpful) {
        if (!reviewRepository.existsById(reviewId))
            throw new ResourceNotFoundException("Review", reviewId);

        ReviewHelpfulness vote = helpfulnessRepository
                .findByUserIdAndReviewId(userId, reviewId)
                .orElse(reviewMapper.helpfulness(userId, reviewId));

        vote.setHelpful(isHelpful);
        helpfulnessRepository.save(vote);

        // Recount and persist
        int yes = helpfulnessRepository.countByReviewIdAndHelpful(reviewId, true);
        int no  = helpfulnessRepository.countByReviewIdAndHelpful(reviewId, false);
        reviewRepository.updateHelpfulness(reviewId, yes, no);
    }

}