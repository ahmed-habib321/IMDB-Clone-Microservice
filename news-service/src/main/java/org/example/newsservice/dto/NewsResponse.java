package org.example.newsservice.dto;

import java.time.Instant;
import java.util.UUID;

public record NewsResponse(
        UUID id,
        String title,
        String slug,
        String excerpt,
        String coverUrl,
        String authorUsername,
        boolean isPublished,
        Instant publishedAt,
        Integer viewCount
) {}
