package org.example.contributionservice.dto;

import org.example.contributionservice.model.enums.GoofType;

import java.util.UUID;

public record GoofResponse(
        UUID id,
        GoofType goofType,
        String body,
        boolean isSpoiler,
        Integer helpfulCount
) {}
