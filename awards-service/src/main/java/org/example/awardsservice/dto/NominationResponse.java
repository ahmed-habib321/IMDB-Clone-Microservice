package org.example.awardsservice.dto;


import org.example.awardsservice.model.enums.AwardOutcome;

import java.util.UUID;

public record NominationResponse(
        UUID id,
        String awardName,
        String awardAbbreviation,
        String category,
        Short year,
        AwardOutcome outcome,
        String notes,
        UUID titleId,
        String titleName,
        UUID personId,
        String personName
) {}
