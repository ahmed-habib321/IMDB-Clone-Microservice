package org.example.titleservice.dto.UpdateTitle;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link org.example.titleservice.model.Season}
 */
public record UpdateSeasonRequest(
        Integer seasonNumber,
        String title,
        String overview,
        String posterUrl,
        LocalDate airDate,
        List<UpdateEpisodeRequest> episodes
){}