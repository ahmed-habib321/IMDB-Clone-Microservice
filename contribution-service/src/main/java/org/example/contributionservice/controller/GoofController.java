package org.example.contributionservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.contributionservice.dto.GoofRequest;
import org.example.contributionservice.dto.GoofResponse;
import org.example.contributionservice.service.GoofService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/titles/{titleId}/goofs")
@RequiredArgsConstructor
@Tag(name = "Goofs")
public class GoofController {

    private final GoofService goofService;

    @GetMapping
    public ResponseEntity<List<GoofResponse>> getGoofs(@PathVariable UUID titleId) {
        return ResponseEntity.ok(goofService.getApproved(titleId));
    }

    @PostMapping
    public ResponseEntity<GoofResponse> addGoof(@PathVariable UUID titleId, @Valid @RequestBody GoofRequest req, @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goofService.create(titleId, userId, req));
    }

    @PutMapping("/{goofId}")
    public ResponseEntity<Void> approve(@PathVariable UUID goofId) {
        goofService.approve(goofId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{goofId}")
    public ResponseEntity<Void> delete(@PathVariable UUID goofId) {
        goofService.delete(goofId);
        return ResponseEntity.noContent().build();
    }
}
