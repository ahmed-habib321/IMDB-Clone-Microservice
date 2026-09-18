package org.example.newsservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.newsservice.dto.CreateNewsArticleRequest;
import org.example.newsservice.dto.NewsDetailResponse;
import org.example.newsservice.dto.NewsResponse;
import org.example.newsservice.dto.UpdateNewsRequest;
import org.example.newsservice.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "News", description = "Entertainment news & articles")
@RequestMapping("/api/v1/news")
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<Page<NewsResponse>> getNews(Pageable pageable) {
        return ResponseEntity.ok(newsService.getNews(pageable));
    }

    @GetMapping("/title/{titleId}")
    public ResponseEntity<Page<NewsResponse>> getNewsByTitle(@PathVariable UUID titleId, Pageable pageable) {
        return ResponseEntity.ok(newsService.getByTitleTag(titleId, pageable));
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<Page<NewsResponse>> getNewsByPerson(@PathVariable UUID personId, Pageable pageable) {
        return ResponseEntity.ok(newsService.getByPersonTag(personId, pageable));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<NewsDetailResponse> getNewsBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(newsService.getBySlug(slug));
    }

    @PostMapping
    public ResponseEntity<NewsResponse> createNews(@Valid @RequestBody CreateNewsArticleRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.create(req));
    }

    @PutMapping("/{slug}")
    public ResponseEntity<NewsResponse> updateNews(@PathVariable String slug, @Valid @RequestBody UpdateNewsRequest req) {
        return ResponseEntity.ok(newsService.update(slug, req));
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deleteNews(@PathVariable String slug) {
        newsService.delete(slug);
        return ResponseEntity.noContent().build();
    }
}