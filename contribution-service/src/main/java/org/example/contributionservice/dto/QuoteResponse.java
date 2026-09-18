package org.example.contributionservice.dto;

import java.util.UUID;

public record QuoteResponse(
        UUID id,
        String text,
        String spokenBy,
        Integer helpfulCount
) {}
