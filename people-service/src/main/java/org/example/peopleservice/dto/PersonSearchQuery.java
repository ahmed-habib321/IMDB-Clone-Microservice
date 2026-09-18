package org.example.peopleservice.dto;

public record PersonSearchQuery(
        String name,
        String gender,
        String department,
        Double minPopularity
) {}
