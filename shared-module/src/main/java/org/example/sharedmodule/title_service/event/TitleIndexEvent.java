package org.example.sharedmodule.title_service.event;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TitleIndexEvent(
        UUID titleId,
        String primaryTitle,
        String originalTitle,
        String slug,
        String titleType,
        String status,
        String overview,
        String posterUrl,
        LocalDate releaseDate,
        Integer runtimeMins,
        Double imdbRating,
        Integer voteCount,
        Double popularity,
        Boolean adult,
        List<String> genres,
        List<String> languages,
        List<String> countries
) {}