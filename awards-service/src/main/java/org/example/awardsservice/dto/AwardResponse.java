package org.example.awardsservice.dto;


import org.example.awardsservice.model.enums.AwardOutcome;

import java.util.UUID;

public record AwardResponse(
        UUID nominationId,
        String awardName,
        String category,
        Short year,
        AwardOutcome outcome,
        String notes
) {}
