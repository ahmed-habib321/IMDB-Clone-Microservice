package org.example.userservice.dto.Request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserProfileRequest(
        @Size(max = 50, message = "Display name must be at most 50 characters")
        String displayName,

        @Size(max = 300, message = "Bio must be at most 300 characters")
        String bio,

        @Size(max = 100, message = "Country must be at most 100 characters")
        String country,

        LocalDate birthDate,
        @Pattern(regexp = "male|female")
        String gender,
        String websiteUrl
) {}
