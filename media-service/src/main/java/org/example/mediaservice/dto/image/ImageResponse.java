package org.example.mediaservice.dto.image;


import org.example.mediaservice.model.enums.ImageType;

import java.util.UUID;

public record ImageResponse(
        UUID id,
        ImageType imageType,
        String url,
        Integer width,
        Integer height,
        Boolean isPrimary
) {}