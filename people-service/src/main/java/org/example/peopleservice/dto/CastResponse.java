package org.example.peopleservice.dto;

import java.util.UUID;

public record CastResponse(
        UUID id,
        UUID personId,
        String personName,
        String personSlug,
        String profileUrl,
        String characterName,
        Integer billingOrder,
        boolean isVoice
) {
}