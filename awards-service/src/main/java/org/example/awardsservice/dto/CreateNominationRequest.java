package org.example.awardsservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.awardsservice.model.enums.AwardOutcome;

import java.util.UUID;

public record CreateNominationRequest(
        @NotNull  UUID         awardId,
        UUID                   titleId,
        String                 titleName,     // denormalized — stored with nomination
        UUID                   personId,
        String                 personName,    // denormalized — stored with nomination
        @NotBlank String       category,
        @NotNull  Short        year,
        @NotNull AwardOutcome outcome,
        String                 notes
) {}
