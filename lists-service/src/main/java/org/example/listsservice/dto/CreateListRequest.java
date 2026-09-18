package org.example.listsservice.dto;


import jakarta.validation.constraints.NotBlank;

public record CreateListRequest(
        @NotBlank String name,
        String description,
        boolean isPublic
) {}
