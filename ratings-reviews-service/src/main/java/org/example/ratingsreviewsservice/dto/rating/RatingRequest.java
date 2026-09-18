package org.example.ratingsreviewsservice.dto.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public record RatingRequest(
        @NotNull
        @Min(value = 1, message = "Score must be at least 1")
        @Max(value = 10, message = "Score must be at most 10")
        Short score
) {}
