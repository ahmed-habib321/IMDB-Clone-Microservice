package org.example.apigateway.dto;

import org.example.apigateway.model.EditorRequestStatus;

import java.time.Instant;
import java.util.UUID;

public record EditorRequestResponse(
        UUID id,
        UUID userId,
        String email,
        EditorRequestStatus status,
        Instant requestedAt,
        Instant reviewedAt
) {}