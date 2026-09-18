package org.example.listsservice.dto;

import java.time.Instant;
import java.util.UUID;

public record UserListResponse(
        UUID id,
        String name,
        String description,
        Boolean isPublic,
        Integer itemCount,
        Instant createdAt
) {}
