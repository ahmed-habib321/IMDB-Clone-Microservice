package org.example.titleservice.dto.UpdateTitle;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for {@link org.example.titleservice.model.Episode}
 */
public record UpdateEpisodeRequest(
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
){}