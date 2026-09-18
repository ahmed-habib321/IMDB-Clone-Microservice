package org.example.contributionservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.contributionservice.dto.TriviaRequest;
import org.example.contributionservice.dto.TriviaResponse;
import org.example.contributionservice.service.TriviaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/titles/{titleId}/trivia")
@RequiredArgsConstructor
@Tag(name = "Trivia")
public class TriviaController {

    private final TriviaService triviaService;

    @GetMapping
    public ResponseEntity<Page<TriviaResponse>> getTrivia(@PathVariable UUID titleId, Pageable pageable) {
        return ResponseEntity.ok(triviaService.getApproved(titleId, pageable));
    }

    @PostMapping
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TriviaResponse> addTrivia(@PathVariable UUID titleId, @Valid @RequestBody TriviaRequest req, @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(triviaService.create(titleId, userId, req));
    }

    @PutMapping("/{triviaId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Void> approve(@PathVariable UUID triviaId) {
        triviaService.approve(triviaId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{triviaId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID triviaId) {
        triviaService.delete(triviaId);
        return ResponseEntity.noContent().build();
    }
}
