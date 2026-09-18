package org.example.titleservice.dto.Responses;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link org.example.titleservice.model.Season}
 */
public record SeasonFullResponse(
        UUID id,
        Integer seasonNumber,
        String title,
        String overview,
        String posterUrl,
        LocalDate airDate,
        List<EpisodeFullResponse> episodes
) implements Serializable {}