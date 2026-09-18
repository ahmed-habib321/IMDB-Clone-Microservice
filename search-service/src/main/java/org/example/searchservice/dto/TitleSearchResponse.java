package org.example.searchservice.dto;

import java.util.UUID;


public record TitleSearchResponse(
        UUID id,
        String primaryTitle,
        String posterUrl,
        Double imdbRating
) {
}
