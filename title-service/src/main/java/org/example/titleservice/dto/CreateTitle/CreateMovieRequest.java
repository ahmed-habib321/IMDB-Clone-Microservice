package org.example.titleservice.dto.CreateTitle;

import jakarta.validation.constraints.NotBlank;
import org.example.sharedmodule.title_service.enums.TitleStatus;
import org.example.sharedmodule.title_service.enums.TitleType;

import java.time.LocalDate;
import java.util.Set;

public record CreateMovieRequest(
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
        LocalDate releaseDate,
        Set<String> genres,
        Set<String> languages,
        Set<String> countries,
        TitleType titleType
) {}
