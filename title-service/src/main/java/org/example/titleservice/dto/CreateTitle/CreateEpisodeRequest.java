package org.example.titleservice.dto.CreateTitle;

import java.time.LocalDate;
import java.util.UUID;

public record CreateEpisodeRequest(
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