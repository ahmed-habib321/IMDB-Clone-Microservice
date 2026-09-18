package org.example.peopleservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record CreatePersonRequest(
        @NotBlank String name,
        List<String> alsoKnownAs,
        String biography,
        String profileUrl,
        LocalDate birthDate,
        LocalDate deathDate,
        String birthPlace,
        String gender,
        Integer heightCm,
        String imdbId
) {}
