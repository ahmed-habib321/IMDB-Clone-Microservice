package org.example.sharedmodule.people_service.event;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PersonIndexEvent(
        UUID personId,
        String name,
        String slug,
        List<String> alsoKnownAs,
        String biography,
        String profileUrl,
        LocalDate birthDate,
        String gender,
        Double popularity,
        String imdbId
) {}