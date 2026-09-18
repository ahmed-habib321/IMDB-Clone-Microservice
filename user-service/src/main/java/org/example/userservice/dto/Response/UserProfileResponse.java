package org.example.userservice.dto.Response;

import java.time.LocalDate;


public record UserProfileResponse(
        String displayName,
        String avatarUrl,
        String bio,
        String country,
        LocalDate birthDate,
        Integer totalRatings,
        Integer totalReviews
) {}
