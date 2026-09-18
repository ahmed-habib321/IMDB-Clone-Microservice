package org.example.peopleservice.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddCastRequest(
        @NotNull UUID personId,
        String characterName,
        Integer billingOrder,
        boolean isVoice,
        Integer episodeCount
) {}
