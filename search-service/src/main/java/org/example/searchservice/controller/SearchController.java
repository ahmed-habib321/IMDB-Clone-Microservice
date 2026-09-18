package org.example.searchservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.searchservice.dto.*;
import org.example.searchservice.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Full-text search across titles and people")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/titles")
    public ResponseEntity<Page<TitleSearchResponse>> searchTitles(@ModelAttribute TitleSearchQuery query, Pageable pageable) {
        return ResponseEntity.ok(searchService.searchTitles(query, pageable));
    }

    @GetMapping("/people")
    public ResponseEntity<Page<PersonSearchResponse>> searchPeople(@ModelAttribute PersonSearchQuery query, Pageable pageable) {
        return ResponseEntity.ok(searchService.searchPeople(query, pageable));
    }

    @GetMapping
    public ResponseEntity<MultiSearchResult> multiSearch(@RequestParam String q) {
        return ResponseEntity.ok(searchService.multiSearch(q));
    }

    @GetMapping("/titles/{titleId}/similar")
    public ResponseEntity<List<TitleSearchResponse>> getSimilar(@PathVariable UUID titleId) {
        return ResponseEntity.ok(searchService.getSimilar(titleId));
    }

}
