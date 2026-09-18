package org.example.listsservice.dto;


import java.time.Instant;
import java.util.UUID;

public record WatchlistItemResponse(
        UUID id,
        UUID titleId,
        String primaryTitle,
        String slug,
        String posterUrl,
        Double imdbRating,
        Boolean watched,
        Instant addedAt,
        Instant watchedAt
) {}
