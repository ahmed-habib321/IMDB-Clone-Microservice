package org.example.searchservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.util.ObjectBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.searchservice.dto.*;
import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.example.sharedmodule.people_service.event.PersonIndexEvent;
import org.example.sharedmodule.search_service.exception.SearchUnavailableException;
import org.example.sharedmodule.title_service.event.TitleIndexEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private static final String TITLE_INDEX = "titles";
    private static final String PEOPLE_INDEX = "people";

    private final ElasticsearchClient es;

    // ====== SEARCH ======

    public Page<TitleSearchResponse> searchTitles(TitleSearchQuery query, Pageable pageable) {
        SearchResponse<TitleIndexEvent> res = search(
                req -> req.index(TITLE_INDEX)
                        .from((int) pageable.getOffset())
                        .size(pageable.getPageSize())
                        .query(SearchQueryFactory.titleSearch(query))
                        .sort(SearchQueryFactory.titleSort(pageable)),
                TitleIndexEvent.class, "searchTitles");

        return page(SearchResultFactory.titleResponses(res.hits().hits()), res, pageable);
    }

    public Page<PersonSearchResponse> searchPeople(PersonSearchQuery query, Pageable pageable) {
        SearchResponse<PersonIndexEvent> res = search(
                req -> req.index(PEOPLE_INDEX)
                        .from((int) pageable.getOffset())
                        .size(pageable.getPageSize())
                        .query(SearchQueryFactory.peopleSearch(query)),
                PersonIndexEvent.class, "searchPeople");

        return page(SearchResultFactory.peopleResponses(res.hits().hits()), res, pageable);
    }

    // ====== MULTI SEARCH ======

    public MultiSearchResult multiSearch(String q) {
        SearchResponse<TitleIndexEvent> titles = search(
                req -> req.index(TITLE_INDEX).size(5).query(SearchQueryFactory.titleSuggest(q)),
                TitleIndexEvent.class, "multiSearch");
        SearchResponse<PersonIndexEvent> people = search(
                req -> req.index(PEOPLE_INDEX).size(5).query(SearchQueryFactory.peopleSuggest(q)),
                PersonIndexEvent.class, "multiSearch");

        return new MultiSearchResult(
                SearchResultFactory.titleResponses(titles.hits().hits()),
                SearchResultFactory.peopleSummaries(people.hits().hits()),
                total(titles), total(people));
    }

    public List<TitleSearchResponse> getSimilar(UUID titleId) {
        TitleIndexEvent source = indexedTitleOrThrow(titleId);
        SearchResponse<TitleIndexEvent> res = search(
                req -> req.index(TITLE_INDEX).size(20)
                        .query(SearchQueryFactory.similarTitles(source, titleId)),
                TitleIndexEvent.class, "getSimilar");

        return SearchResultFactory.titleResponses(res.hits().hits());
    }

    // ====== SUPPORT ======

    private <T> SearchResponse<T> search(
            Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> request,
            Class<T> resultType,
            String operation) {
        try {
            return es.search(request, resultType);
        } catch (IOException e) {
            log.error("ES {} failed: {}", operation, e.getMessage(), e);
            throw new SearchUnavailableException("Search is temporarily unavailable.", e);
        }
    }

    private TitleIndexEvent indexedTitleOrThrow(UUID titleId) {
        try {
            GetResponse<TitleIndexEvent> res = es.get(
                    g -> g.index(TITLE_INDEX).id(titleId.toString()), TitleIndexEvent.class);
            if (!res.found() || res.source() == null)
                throw new ResourceNotFoundException("Title not indexed: " + titleId);
            return res.source();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (IOException e) {
            log.error("ES getSimilar failed for {}: {}", titleId, e.getMessage(), e);
            throw new SearchUnavailableException("Search is temporarily unavailable.", e);
        }
    }

    private static long total(SearchResponse<?> res) {
        return res.hits().total() != null ? res.hits().total().value() : 0;
    }

    private static <T> Page<T> page(List<T> content, SearchResponse<?> res, Pageable pageable) {
        return new PageImpl<>(content, pageable, total(res));
    }
}