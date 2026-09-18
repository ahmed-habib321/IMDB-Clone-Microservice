package org.example.ratingsreviewsservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ratingsreviewsservice.dto.rating.RatingDistribution;
import org.example.ratingsreviewsservice.dto.rating.RatingRequest;
import org.example.ratingsreviewsservice.dto.rating.RatingResponse;
import org.example.ratingsreviewsservice.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Ratings", description = "Title rating operations")
@RequestMapping("/api/v1/titles/{titleId}/ratings")
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    public ResponseEntity<RatingResponse> getRatings(@PathVariable UUID titleId) {
        return ResponseEntity.ok(ratingService.getRatings(titleId));
    }

    @GetMapping("/distribution")
    public ResponseEntity<RatingDistribution> getDistribution(@PathVariable UUID titleId) {
        return ResponseEntity.ok(ratingService.getDistribution(titleId));
    }

    @PostMapping
    public ResponseEntity<RatingResponse> addOrUpdateRating(
            @PathVariable UUID titleId,
            @Valid @RequestBody RatingRequest req,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ratingService.addOrUpdateRating(titleId, req, userId));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteRating(
            @PathVariable UUID titleId,
            @RequestHeader("X-User-Id") UUID userId) {
        ratingService.deleteRating(titleId, userId);
        return ResponseEntity.noContent().build();
    }
}