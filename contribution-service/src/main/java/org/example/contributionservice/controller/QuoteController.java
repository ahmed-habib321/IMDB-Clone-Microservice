package org.example.contributionservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.contributionservice.dto.QuoteRequest;
import org.example.contributionservice.dto.QuoteResponse;
import org.example.contributionservice.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/titles/{titleId}/quotes")
@RequiredArgsConstructor
@Tag(name = "Quotes")
public class QuoteController {

    private final QuoteService quoteService;

    @GetMapping
    public ResponseEntity<List<QuoteResponse>> getQuotes(@PathVariable UUID titleId) {
        return ResponseEntity.ok(quoteService.getApproved(titleId));
    }

    @PostMapping
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuoteResponse> addQuote(
            @PathVariable UUID titleId,
            @Valid @RequestBody QuoteRequest req,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quoteService.create(titleId, userId, req));
    }

    @PatchMapping("/{quoteId}/approve")
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Void> approve(@PathVariable UUID quoteId) {
        quoteService.approve(quoteId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{quoteId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID quoteId) {
        quoteService.delete(quoteId);
        return ResponseEntity.noContent().build();
    }
}