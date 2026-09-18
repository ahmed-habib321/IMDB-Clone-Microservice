package org.example.ratingsreviewsservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ratingsreviewsservice.dto.review.ReviewRequest;
import org.example.ratingsreviewsservice.dto.review.ReviewResponse;
import org.example.ratingsreviewsservice.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Title review operations")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/v1/titles/{titleId}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @PathVariable UUID titleId,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviews(titleId, pageable));
    }

    @PostMapping("/api/v1/titles/{titleId}/reviews")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponse> addReview(
            @PathVariable UUID titleId,
            @Valid @RequestBody ReviewRequest req,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.addReview(userId,titleId, req));
    }

    @PutMapping("/api/v1/reviews/{reviewId}/approve")
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ReviewResponse> approveReview(@PathVariable UUID reviewId) {
        return ResponseEntity.ok(reviewService.approveReview(reviewId));
    }

    @PostMapping("/api/v1/reviews/{reviewId}/helpfulness")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> voteHelpful(
            @PathVariable UUID reviewId,
            @RequestParam boolean helpful,
            @RequestHeader("X-User-Id") UUID userId) {
        reviewService.voteHelpful(userId, reviewId, helpful);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/v1/users/{userId}/reviews")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ReviewResponse>> getUserReviews(
            @PathVariable UUID userId,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getUserReviews(userId, pageable));
    }
}
