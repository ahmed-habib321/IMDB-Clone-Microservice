package org.example.listsservice.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MarkWatchedRequest(
        @NotNull UUID titleId,
        @NotNull Boolean watched
) {}