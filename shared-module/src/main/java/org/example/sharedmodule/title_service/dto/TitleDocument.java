package org.example.sharedmodule.title_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TitleDocument {
    private String id;
    private String primaryTitle;
    private String originalTitle;
    private String titleType;
    private String status;
    private LocalDate releaseDate;
    private Boolean adult;
    private Double imdbRating;
    private Set<String> genres;
    private Set<String> languages;
    private Set<String> countries;
}
