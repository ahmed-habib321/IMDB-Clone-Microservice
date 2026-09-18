package org.example.mediaservice.dto.trailer;

import java.time.Instant;
import java.util.UUID;

public record TrailerResponse(
        UUID id,
        String name,
        String trailerType,
        String youtubeKey,
        Integer durationSecs,
        String language,
        Instant publishedAt
) {}