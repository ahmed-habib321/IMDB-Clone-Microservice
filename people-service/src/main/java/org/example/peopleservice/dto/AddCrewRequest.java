package org.example.peopleservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddCrewRequest(
        @NotNull UUID personId,
        @NotBlank String department,
        @NotBlank String job
) {}
