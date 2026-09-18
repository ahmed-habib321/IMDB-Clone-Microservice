package org.example.titleservice.dto.UpdateTitle;

import org.example.sharedmodule.title_service.enums.TitleStatus;
import org.example.sharedmodule.title_service.enums.TitleType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record UpdateTvShowRequest(
        String primaryTitle,
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
        String network,
        Integer totalSeasons,
        Integer totalEpisodes,
        Integer episodeRuntime,
        Boolean isOnGoing,
        LocalDate releaseDate,
        LocalDate finishedAt,
        Set<String> genres,
        Set<String> languages,
        Set<String> countries,
        TitleType titleType,
        Double imdbRating,
        Integer voteCount,
        Double popularity,
        List<UpdateSeasonRequest> seasons
) {}
