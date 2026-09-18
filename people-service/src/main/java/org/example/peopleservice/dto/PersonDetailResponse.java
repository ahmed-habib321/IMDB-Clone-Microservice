package org.example.peopleservice.dto;


import org.example.sharedmodule.title_service.dto.TitleMiniResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PersonDetailResponse(
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
        List<TitleMiniResponse> recentWork
) {}
