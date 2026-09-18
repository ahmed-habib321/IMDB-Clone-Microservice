package org.example.searchservice.dto;

public record PersonSearchQuery(
        String name,
        String gender,
        String department,
        Double minPopularity
) {}
