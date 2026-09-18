package org.example.peopleservice.dto;

import java.util.UUID;

public record CrewResponse(
        UUID id,
        UUID personId,
        String personName,
        String personSlug,
        String profileUrl,
        String department,
        String job
) {}
