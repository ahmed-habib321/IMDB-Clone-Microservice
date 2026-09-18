package org.example.newsservice.dto;

import java.util.List;
import java.util.UUID;

public record UpdateNewsRequest(
        String title,
        String excerpt,
        String body,
        String coverUrl,
        List<UUID> taggedTitleIds,
        List<UUID> taggedPersonIds
) {}
