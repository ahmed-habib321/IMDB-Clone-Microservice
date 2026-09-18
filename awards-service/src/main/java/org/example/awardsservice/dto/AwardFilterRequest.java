package org.example.awardsservice.dto;

public record AwardFilterRequest(
        String awardName,
        Integer year,
        String category,
        String outcome
) {}