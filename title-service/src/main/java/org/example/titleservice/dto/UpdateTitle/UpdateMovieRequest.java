package org.example.titleservice.dto.UpdateTitle;

import org.example.sharedmodule.title_service.enums.TitleStatus;
import org.example.sharedmodule.title_service.enums.TitleType;

import java.time.LocalDate;
import java.util.Set;

public record UpdateMovieRequest(
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
        LocalDate releaseDate,
        Set<String> genres,
        Set<String> languages,
        Set<String> countries,
        Double imdbRating,
        Integer voteCount,
        Double popularity,
        TitleType titleType
){}
