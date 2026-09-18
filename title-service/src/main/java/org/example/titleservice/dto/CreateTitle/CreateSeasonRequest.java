package org.example.titleservice.dto.CreateTitle;

import java.time.LocalDate;
import java.util.List;

public record CreateSeasonRequest(
        Integer seasonNumber,
        String title,
        String overview,
        String posterUrl,
        LocalDate airDate,
        List<CreateEpisodeRequest> episodes
) {}