package org.example.contributionservice.dto;

import jakarta.validation.constraints.NotBlank;

public record QuoteRequest(
        @NotBlank String text,
        String spokenBy
) {}
