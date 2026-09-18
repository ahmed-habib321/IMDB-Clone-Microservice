package org.example.mediaservice.dto.trailer;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record TrailerRequest(
        @NotBlank String name,
        String trailerType,
        String youtubeKey,
        Integer durationSecs,
        String language,
        Instant publishedAt
) {}
