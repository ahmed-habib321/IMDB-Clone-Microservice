package org.example.listsservice.dto;

public record UpdateListRequest(
        String name,
        String description,
        Boolean isPublic
) {}
