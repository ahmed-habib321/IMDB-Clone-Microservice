package org.example.peopleservice.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PersonResponse(
        UUID id,
        String name,
        String slug,
        String profileUrl,
        LocalDate birthDate,
        LocalDate deathDate,
        String birthPlace,
        String gender,
        Double popularity
) {}
