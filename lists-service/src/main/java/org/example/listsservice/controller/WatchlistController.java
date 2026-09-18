package org.example.listsservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.listsservice.dto.MarkWatchedRequest;
import org.example.listsservice.dto.WatchlistItemResponse;
import org.example.listsservice.service.WatchlistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/watchlist")
@RequiredArgsConstructor
@Tag(name = "Watchlist", description = "Personal title watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    @GetMapping
    public ResponseEntity<Page<WatchlistItemResponse>> getWatchlist(@RequestHeader("X-User-Id") UUID userId, Pageable pageable) {
        return ResponseEntity.ok(watchlistService.getWatchlist(userId, pageable));
    }

    @GetMapping("/contains")
    public ResponseEntity<Boolean> contains(@RequestParam UUID titleId, @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(watchlistService.isInWatchlist(userId, titleId));
    }

    @PostMapping
    public ResponseEntity<WatchlistItemResponse> add(@RequestBody UUID titleId, @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(watchlistService.addToWatchlist(userId, titleId));
    }

    @DeleteMapping
    public ResponseEntity<Void> remove(@RequestBody UUID titleId, @RequestHeader("X-User-Id") UUID userId) {
        watchlistService.removeFromWatchlist(userId, titleId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<WatchlistItemResponse> markWatched(@Valid @RequestBody MarkWatchedRequest req, @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(watchlistService.markWatched(userId, req.titleId(), req.watched()));
    }
}