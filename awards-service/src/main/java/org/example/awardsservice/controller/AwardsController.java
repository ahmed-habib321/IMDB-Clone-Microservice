package org.example.awardsservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.awardsservice.dto.*;
import org.example.awardsservice.service.AwardsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/awards")
@RequiredArgsConstructor
@Tag(name = "Awards", description = "Award ceremonies, nominations, and winners")
public class AwardsController {

    private final AwardsService awardsService;

    @GetMapping
    public ResponseEntity<Page<AwardResponse>> getAwards(@ModelAttribute AwardFilterRequest filter, Pageable pageable) {
        return ResponseEntity.ok(awardsService.getTitles(filter, pageable));
    }

    @GetMapping("/titles/{titleId}")
    public ResponseEntity<List<NominationResponse>> getTitleAwards(@PathVariable UUID titleId) {
        return ResponseEntity.ok(awardsService.getTitleNominations(titleId));
    }

    @GetMapping("/people/{personId}")
    public ResponseEntity<List<NominationResponse>> getPersonAwards(@PathVariable UUID personId) {
        return ResponseEntity.ok(awardsService.getPersonNominations(personId));
    }

    @PostMapping("/nominations")
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<NominationResponse> addNomination(@Valid @RequestBody CreateNominationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(awardsService.createNomination(req));
    }

    @GetMapping("/top-winners")
    public ResponseEntity<List<TopWinnerResponse>> getTopWinners(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String awardName) {
        return ResponseEntity.ok(awardsService.getTopWinners(year, awardName));
    }
}
