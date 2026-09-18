package org.example.listsservice.dto;


import java.util.UUID;

public record ListResponse(
        UUID id,
        String name,
        Integer itemCount
) {}
