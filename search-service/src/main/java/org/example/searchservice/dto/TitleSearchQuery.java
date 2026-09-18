package org.example.searchservice.dto;


import lombok.Builder;

@Builder
public record TitleSearchQuery (
    String q,
    String titleType,
    String genre,
    String language,
    String country,
    Boolean adult,
    String status,
    Double minRating,
    Integer yearFrom,
    Integer yearTo
){}
