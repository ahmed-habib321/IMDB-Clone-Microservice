package org.example.mediaservice.dto.image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.mediaservice.model.enums.ImageType;

public record ImageRequest(
        @NotNull ImageType imageType,
        @NotBlank String url,
        Integer width,
        Integer height,
        Boolean isPrimary
) {}