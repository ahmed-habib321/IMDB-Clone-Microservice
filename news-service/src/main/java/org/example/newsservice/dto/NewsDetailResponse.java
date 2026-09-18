package org.example.newsservice.dto;

import java.time.Instant;
import java.util.UUID;

public record NewsDetailResponse(
        UUID id,
        String title,
        String slug,
        String excerpt,
        String body,
        String coverUrl,
        String authorUsername,
        boolean isPublished,
        Instant publishedAt,
        Integer viewCount,
        Instant createdAt
) {}
