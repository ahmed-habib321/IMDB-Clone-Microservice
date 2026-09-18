package org.example.searchservice.dto;

import java.util.List;

public record MultiSearchResult(
        List<TitleSearchResponse> titles,
        List<PersonSearchResponse> people,
        long totalTitles,
        long totalPeople
) {}
