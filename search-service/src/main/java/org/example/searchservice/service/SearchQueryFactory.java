package org.example.searchservice.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import org.example.searchservice.dto.PersonSearchQuery;
import org.example.searchservice.dto.TitleSearchQuery;
import org.example.sharedmodule.title_service.event.TitleIndexEvent;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Builds the Elasticsearch {@link Query} and {@link SortOptions} objects from the
 * API query DTOs. Keeps the ES query DSL out of {@link SearchService} so that the
 * service reads as a plain list of search operations.
 */
final class SearchQueryFactory {

    private SearchQueryFactory() {
    }

    // ===== TITLES =====

    static Query titleSearch(TitleSearchQuery q) {
        return Query.of(query -> query.bool(bool -> {
            if (hasText(q.q())) {
                bool.must(m -> m.multiMatch(multi -> multi
                        .fields("primaryTitle^5", "originalTitle^3", "overview^1")
                        .query(q.q())
                        .fuzziness("AUTO")));
            }
            termFilter(bool, "titleType", q.titleType());
            termFilter(bool, "genres.keyword", q.genre());
            termFilter(bool, "languages.keyword", q.language());
            termFilter(bool, "countries.keyword", q.country());
            termFilter(bool, "status.keyword", q.status());
            if (q.adult() != null)
                bool.filter(f -> f.term(t -> t.field("adult").value(q.adult())));
            if (q.minRating() != null)
                bool.filter(f -> f.range(r -> r.number(n -> n.field("imdbRating").gte(q.minRating()))));
            yearFilter(bool, q.yearFrom(), q.yearTo());
            return bool;
        }));
    }

    static Query similarTitles(TitleIndexEvent source, UUID excludeTitleId) {
        List<String> genres = source.genres() != null ? source.genres() : List.of();
        return Query.of(query -> query.bool(bool -> bool
                .must(m -> m.term(t -> t.field("titleType").value(source.titleType())))
                .mustNot(n -> n.term(t -> t.field("titleId").value(excludeTitleId.toString())))
                .should(sh -> sh.terms(t -> t.field("genres.keyword")
                        .terms(TermsQueryField.of(f -> f.value(genres.stream().map(FieldValue::of).toList())))))
                .filter(f -> f.range(r -> r.number(n -> n.field("imdbRating").gte(6.0))))
                .minimumShouldMatch("1")));
    }

    static List<SortOptions> titleSort(Pageable pageable) {
        String sortField = pageable.getSort().isSorted()
                ? pageable.getSort().iterator().next().getProperty()
                : "relevance";
        return List.of(switch (sortField) {
            case "imdbRating" -> SortOptions.of(so -> so.field(f -> f.field("imdbRating").order(SortOrder.Desc)));
            case "startYear"  -> SortOptions.of(so -> so.field(f -> f.field("releaseDate").order(SortOrder.Desc)));
            case "popularity" -> SortOptions.of(so -> so.field(f -> f.field("popularity").order(SortOrder.Desc)));
            default           -> SortOptions.of(so -> so.score(sc -> sc.order(SortOrder.Desc)));
        });
    }

    // ===== PEOPLE =====

    static Query peopleSearch(PersonSearchQuery q) {
        return Query.of(query -> query.bool(bool -> {
            if (hasText(q.name())) {
                bool.must(m -> m.multiMatch(multi -> multi
                        .fields("name^3", "alsoKnownAs^1", "biography")
                        .query(q.name())
                        .fuzziness("AUTO")));
            }
            if (q.gender() != null)
                bool.filter(f -> f.term(t -> t.field("gender").value(q.gender())));
            if (q.minPopularity() != null)
                bool.filter(f -> f.range(r -> r.number(n -> n.field("popularity").gte(q.minPopularity()))));
            return bool;
        }));
    }

    // ===== MULTI SEARCH =====

    static Query titleSuggest(String term) {
        return Query.of(query -> query.multiMatch(multi -> multi
                .fields("primaryTitle^5", "originalTitle^2")
                .query(term)
                .fuzziness("AUTO")));
    }

    static Query peopleSuggest(String term) {
        return Query.of(query -> query.multiMatch(multi -> multi
                .fields("name^3", "alsoKnownAs")
                .query(term)
                .fuzziness("AUTO")));
    }

    // ===== INTERNALS =====

    private static void termFilter(BoolQuery.Builder bool, String field, String value) {
        if (value != null)
            bool.filter(f -> f.term(t -> t.field(field).value(value)));
    }

    private static void yearFilter(BoolQuery.Builder bool, Integer from, Integer to) {
        if (from == null && to == null)
            return;
        bool.filter(f -> f.range(r -> r.date(d -> {
            if (from != null) d.gte(from + "-01-01");
            if (to != null)   d.lte(to + "-12-31");
            return d.field("releaseDate");
        })));
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}