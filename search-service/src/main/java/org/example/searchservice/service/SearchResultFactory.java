package org.example.searchservice.service;

import co.elastic.clients.elasticsearch.core.search.Hit;
import org.example.searchservice.dto.PersonSearchResponse;
import org.example.searchservice.dto.TitleSearchResponse;
import org.example.sharedmodule.people_service.event.PersonIndexEvent;
import org.example.sharedmodule.title_service.event.TitleIndexEvent;

import java.util.List;
import java.util.Objects;

/**
 * Maps Elasticsearch hits (the index events) into the API response DTOs.
 */
final class SearchResultFactory {

    private SearchResultFactory() {
    }

    static List<TitleSearchResponse> titleResponses(List<Hit<TitleIndexEvent>> hits) {
        return hits.stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(SearchResultFactory::toTitleResponse)
                .toList();
    }

    static List<PersonSearchResponse> peopleResponses(List<Hit<PersonIndexEvent>> hits) {
        return hits.stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(SearchResultFactory::toPersonResponse)
                .toList();
    }

    /** Compact form used by the quick multi-search suggestions. */
    static List<PersonSearchResponse> peopleSummaries(List<Hit<PersonIndexEvent>> hits) {
        return hits.stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(SearchResultFactory::toPersonSummary)
                .toList();
    }

    private static TitleSearchResponse toTitleResponse(TitleIndexEvent e) {
        return new TitleSearchResponse(e.titleId(), e.primaryTitle(), e.posterUrl(), e.imdbRating());
    }

    private static PersonSearchResponse toPersonResponse(PersonIndexEvent e) {
        return PersonSearchResponse.builder()
                .id(e.personId())
                .name(e.name())
                .slug(e.slug())
                .alsoKnownAs(e.alsoKnownAs())
                .biography(e.biography())
                .profileUrl(e.profileUrl())
                .birthDate(e.birthDate())
                .gender(e.gender())
                .popularity(e.popularity())
                .imdbId(e.imdbId())
                .recentWork(List.of()) // enabled by a separate title-service call
                .build();
    }

    private static PersonSearchResponse toPersonSummary(PersonIndexEvent e) {
        return PersonSearchResponse.builder()
                .id(e.personId())
                .name(e.name())
                .slug(e.slug())
                .profileUrl(e.profileUrl())
                .popularity(e.popularity())
                .recentWork(List.of())
                .build();
    }
}