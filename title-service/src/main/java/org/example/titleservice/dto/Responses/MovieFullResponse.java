package org.example.titleservice.dto.Responses;

import lombok.Builder;
import org.example.sharedmodule.title_service.enums.TitleStatus;
import org.example.sharedmodule.title_service.enums.TitleType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
public record MovieFullResponse(
        UUID id,
        TitleType titleType,
        String primaryTitle,
        String originalTitle,
        String slug,
        String tagline,
        String overview,
        String posterUrl,
        String backdropUrl,
        TitleStatus status,
        Integer runtimeMins,
        Long budget,
        Long revenue,
        Double imdbRating,
        Integer voteCount,
        Double popularity,
        Boolean adult,
        LocalDate releaseDate,
        Set<String> genres,
        Set<String> languages,
        Set<String> countries
) implements Serializable {}
