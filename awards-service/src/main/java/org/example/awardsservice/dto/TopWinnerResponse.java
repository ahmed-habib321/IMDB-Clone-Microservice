package org.example.awardsservice.dto;

import java.util.UUID;

public record TopWinnerResponse(
        UUID id,
        String name,
        String type,
        Integer winsCount,
        Integer nominationsCount
) {}
