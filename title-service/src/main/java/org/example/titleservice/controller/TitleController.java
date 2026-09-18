package org.example.titleservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.sharedmodule.title_service.dto.TitleMiniResponse;
import org.example.titleservice.dto.CreateTitle.CreateMovieRequest;
import org.example.titleservice.dto.CreateTitle.CreateTvShowRequest;
import org.example.titleservice.dto.Responses.*;
import org.example.titleservice.dto.UpdateTitle.UpdateMovieRequest;
import org.example.titleservice.dto.UpdateTitle.UpdateTvShowRequest;
import org.example.titleservice.service.TitleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/title")
@RequiredArgsConstructor
@Tag(name = "Titles", description = "Movies, TV Shows & other title types")
public class TitleController {

    private final TitleService titleService;

    // ----- Movie --------------------------

    @GetMapping("/movie/{id}")
    public ResponseEntity<MovieFullResponse> getMovie(@PathVariable String id) {
        return ResponseEntity.ok(titleService.getMovie(id));
    }

    @PostMapping("/movie")
    public ResponseEntity<Void> createMovie(@Valid @RequestBody CreateMovieRequest req) {
        titleService.createMovie(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/movie/{id}")
    public ResponseEntity<Void> updateMovie(@PathVariable String id, @Valid @RequestBody UpdateMovieRequest req) {
        titleService.updateMovie(id, req);
        return ResponseEntity.noContent().build();
    }


    // ----- Show --------------------------

    @GetMapping("/show/{id}")
    public ResponseEntity<ShowFullResponse> getShow(@PathVariable String id) {
        return ResponseEntity.ok(titleService.getShow(id));
    }

    @PostMapping("/show")
    public ResponseEntity<Void> createShow(@Valid @RequestBody CreateTvShowRequest req) {
        titleService.createShow(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/show/{id}")
    public ResponseEntity<Void> updateShow(@PathVariable String id,@Valid @RequestBody UpdateTvShowRequest req) {
        titleService.updateShow(id, req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/show/{id}/seasons")
    public ResponseEntity<Page<SeasonFullResponse>> getSeasons(@PathVariable String id, Pageable pageable) {
        return ResponseEntity.ok(titleService.getSeasons(id, pageable));
    }

    @GetMapping("/show/{id}/{sn}")
    public ResponseEntity<Page<EpisodeFullResponse>> getEpisodes(@PathVariable String id, @PathVariable short sn, Pageable pageable) {
        return ResponseEntity.ok(titleService.getEpisodes(id, sn, pageable));
    }

    @GetMapping("/show/{id}/{sn}/{ep}")
    public ResponseEntity<EpisodeFullResponse> getEpisode(@PathVariable String id,@PathVariable short sn,@PathVariable short ep) {
        return ResponseEntity.ok(titleService.getEpisode(id, sn, ep));
    }



    // ----- Home / listing endpoints --------------------------

    @GetMapping("/trending")
    public ResponseEntity<List<TitleCardResponse>> getTrending() {
        return ResponseEntity.ok(titleService.getTrendingTitles());
    }

    @GetMapping("/featured")
    public ResponseEntity<List<TitleCardResponse>> getFeatured() {
        return ResponseEntity.ok(titleService.getFeaturedTitles());
    }

    @GetMapping("/new-releases")
    public ResponseEntity<List<TitleCardResponse>> getNewReleases() {
        return ResponseEntity.ok(titleService.getNewReleases());
    }

    @GetMapping("/genres")
    public ResponseEntity<List<String>> getGenres() {
        return ResponseEntity.ok(titleService.getGenres());
    }



    // ----- Title --------------------------

    @PostMapping("/search")
    public ResponseEntity<List<TitleMiniResponse>> getTitlesForSearch(@RequestBody List<String> ids) {
        return ResponseEntity.ok(titleService.getTitlesForSearch(ids));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTitle(@PathVariable String id) {
        titleService.deleteTitle(id);
        return ResponseEntity.noContent().build();
    }

}
