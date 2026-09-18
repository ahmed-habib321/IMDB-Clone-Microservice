package org.example.contributionservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.contributionservice.model.enums.GoofType;

public record GoofRequest(
        @NotNull GoofType goofType,
        @NotBlank String body,
        boolean isSpoiler
) {}
