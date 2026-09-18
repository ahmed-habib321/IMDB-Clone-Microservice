package org.example.titleservice.dto.Responses;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for {@link org.example.titleservice.model.Episode}
 */
public record EpisodeFullResponse(
        UUID id,
        Integer episodeNumber,
        String title,
        String overview,
        String stillUrl,
        LocalDate airDate,
        Integer runtimeMins,
        Double imdbRating,
        Integer voteCount,
        UUID writerId,
        UUID directorId
) implements Serializable {}