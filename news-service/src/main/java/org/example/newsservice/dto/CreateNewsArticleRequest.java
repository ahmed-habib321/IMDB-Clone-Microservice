package org.example.newsservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record CreateNewsArticleRequest(
        @NotBlank String title,
        @NotBlank String body,
        @NotBlank String authorUsername,
        List<UUID> taggedTitleIds,
        List<UUID> taggedPersonIds
) {}
