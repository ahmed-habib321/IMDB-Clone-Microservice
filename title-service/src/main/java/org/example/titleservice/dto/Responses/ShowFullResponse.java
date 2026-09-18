package org.example.titleservice.dto.Responses;

import lombok.Builder;
import org.example.sharedmodule.title_service.enums.TitleStatus;
import org.example.sharedmodule.title_service.enums.TitleType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
public record ShowFullResponse(
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
        LocalDate releaseDate,
        LocalDate finishedAt,
        Integer runtimeMins,
        Long budget,
        Long revenue,
        Double imdbRating,
        Integer voteCount,
        Double popularity,
        Boolean adult,
        String network,
        UUID creatorId,
        Integer totalSeasons,
        Integer totalEpisodes,
        Integer episodeRuntime,
        Boolean isOnGoing,
        Set<String> genres,
        Set<String> languages,
        Set<String> countries,
        List<SeasonFullResponse> seasons
) implements Serializable {}