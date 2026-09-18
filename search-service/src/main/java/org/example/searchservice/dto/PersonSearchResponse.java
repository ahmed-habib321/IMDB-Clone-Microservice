package org.example.searchservice.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record PersonSearchResponse(
        UUID id,
        String name,
        String slug,
        List<String> alsoKnownAs,
        String biography,
        String profileUrl,
        LocalDate birthDate,
        LocalDate deathDate,
        String birthPlace,
        String gender,
        Integer heightCm,
        Double popularity,
        String imdbId,
        List<TitleSearchResponse> recentWork
) {}