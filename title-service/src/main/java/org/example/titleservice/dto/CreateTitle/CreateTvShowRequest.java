package org.example.titleservice.dto.CreateTitle;

import jakarta.validation.constraints.NotBlank;
import org.example.sharedmodule.title_service.enums.TitleStatus;
import org.example.sharedmodule.title_service.enums.TitleType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record CreateTvShowRequest(
        @NotBlank String primaryTitle,
        String originalTitle,
        String tagline,
        String overview,
        String posterUrl,
        String backdropUrl,
        TitleStatus status,
        Integer runtimeMins,
        Long budget,
        Long revenue,
        Boolean adult,
        Set<String> genres,
        Set<String> languages,
        Set<String> countries,
        String network,
        UUID creatorId,
        Integer totalSeasons,
        Integer totalEpisodes,
        Integer episodeRuntime,
        Boolean isOnGoing,
        List<CreateSeasonRequest> seasons,
        LocalDate releaseDate,
        LocalDate finishedAt,
        TitleType titleType) {}