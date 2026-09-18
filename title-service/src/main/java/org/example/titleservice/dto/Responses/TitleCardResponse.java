package org.example.titleservice.dto.Responses;

import lombok.Builder;
import org.example.sharedmodule.title_service.enums.TitleStatus;
import org.example.sharedmodule.title_service.enums.TitleType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
public record TitleCardResponse(
        UUID id,
        TitleType titleType,
        String primaryTitle,
        String slug,
        String overview,
        String posterUrl,
        String backdropUrl,
        TitleStatus status,
        LocalDate releaseDate,
        Double imdbRating,
        Integer voteCount,
        Double popularity,
        Set<String> genres
) implements Serializable {}