package org.example.ratingsreviewsservice.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
        @NotBlank
        @Size(max = 200, message = "Review title must be at most 200 characters")
        String reviewTitle,

        @NotBlank
        @Size(min = 50, max = 5000, message = "Review body must be between 50 and 5000 characters")
        String body,

        boolean containsSpoiler
) {}
