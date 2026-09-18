package org.example.ratingsreviewsservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ratingsreviewsservice.dto.rating.UserRatingResponse;
import org.example.ratingsreviewsservice.service.RatingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Ratings", description = "A user's rating history")
@RequestMapping("/api/v1/users/{userId}/ratings")
public class UserRatingController {

    private final RatingService ratingService;

    @GetMapping
    public ResponseEntity<Page<UserRatingResponse>> getUserRatings(@PathVariable UUID userId, Pageable pageable) {
        return ResponseEntity.ok(ratingService.getUserRatings(userId, pageable));
    }
}