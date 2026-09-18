package org.example.listsservice.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserListDetailResponse(
        UUID id,
        String name,
        String description,
        Boolean isPublic,
        Integer itemCount,
        Instant createdAt,
        List<UUID> titleIds
) {}
