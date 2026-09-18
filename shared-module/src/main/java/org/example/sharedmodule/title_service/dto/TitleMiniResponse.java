package org.example.sharedmodule.title_service.dto;

import org.example.sharedmodule.title_service.enums.TitleStatus;
import org.example.sharedmodule.title_service.enums.TitleType;

import java.time.LocalDate;
import java.util.UUID;

public record TitleMiniResponse(
        UUID id,
        String primaryTitle,
        String posterUrl,
        LocalDate releaseDate,
        TitleStatus status,
        TitleType titleType,
        Double imdbRating
) {}