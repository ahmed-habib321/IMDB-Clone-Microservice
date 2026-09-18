package org.example.contributionservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TriviaRequest(
        @NotBlank @Size(max = 2000) String body,
        boolean isSpoiler
) {}