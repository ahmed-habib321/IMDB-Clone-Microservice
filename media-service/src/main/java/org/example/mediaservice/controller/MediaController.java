package org.example.mediaservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mediaservice.dto.boxOffice.BoxOfficeDTO;
import org.example.mediaservice.dto.image.ImageRequest;
import org.example.mediaservice.dto.image.ImageResponse;
import org.example.mediaservice.dto.trailer.TrailerRequest;
import org.example.mediaservice.dto.trailer.TrailerResponse;
import org.example.mediaservice.model.enums.ImageType;
import org.example.mediaservice.service.BoxOfficeService;
import org.example.mediaservice.service.MediaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Media", description = "Images, trailers, and box office data")
public class MediaController {


    private final MediaService mediaService;
    private final BoxOfficeService boxOfficeService;

    // ── Title Images ──────────────────────────────────────────────────

    @GetMapping("/api/v1/titles/{titleId}/images")
    public ResponseEntity<List<ImageResponse>> getTitleImages(@PathVariable UUID titleId) {
        return ResponseEntity.ok(mediaService.getTitleImages(titleId));
    }

    @PostMapping("/api/v1/titles/{titleId}/images")
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ImageResponse> addTitleImage(
            @PathVariable UUID titleId,
            @Valid @RequestBody ImageRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mediaService.addTitleImage(titleId, req));
    }

    @PostMapping(value = "/api/v1/titles/{titleId}/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ImageResponse> uploadTitleImage(@PathVariable UUID titleId, @RequestParam ImageType type, @RequestParam MultipartFile file, @RequestHeader("X-User-Id") UUID uploadedBy) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediaService.uploadTitleImage(titleId, uploadedBy, file, type));
    }

    // ── Person Images ─────────────────────────────────────────────────

    @GetMapping("/api/v1/people/{personId}/images")
    public ResponseEntity<List<ImageResponse>> getPersonImages(@PathVariable UUID personId) {
        return ResponseEntity.ok(mediaService.getPersonImages(personId));
    }

    @PostMapping("/api/v1/people/{personId}/images")
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ImageResponse> addPersonImage(@PathVariable UUID personId, @Valid @RequestBody ImageRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediaService.addPersonImage(personId, req));
    }

    @PostMapping(value = "/api/v1/people/{personId}/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ImageResponse> uploadPersonImage(@PathVariable UUID personId, @RequestParam ImageType type, @RequestParam MultipartFile file, @RequestHeader("X-User-Id") UUID uploadedBy) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediaService.uploadPersonImage(personId, uploadedBy, file, type));
    }

    // ── Trailers ──────────────────────────────────────────────────────

    @GetMapping("/api/v1/titles/{titleId}/trailers")
    public ResponseEntity<List<TrailerResponse>> getTrailers(@PathVariable UUID titleId) {
        return ResponseEntity.ok(mediaService.getTrailers(titleId));
    }

    @PostMapping("/api/v1/titles/{titleId}/trailers")
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<TrailerResponse> addTrailer(@PathVariable UUID titleId, @Valid @RequestBody TrailerRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediaService.addTrailer(titleId, req));
    }

    // ── Box Office ────────────────────────────────────────────────────

    @GetMapping("/api/v1/titles/{titleId}/box-office")
    public ResponseEntity<BoxOfficeDTO> getBoxOffice(@PathVariable UUID titleId) {
        return ResponseEntity.ok(boxOfficeService.getBoxOffice(titleId));
    }

    @PutMapping("/api/v1/titles/{titleId}/box-office")
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<BoxOfficeDTO> upsertBoxOffice(@PathVariable UUID titleId, @Valid @RequestBody BoxOfficeDTO req) {
        return ResponseEntity.ok(boxOfficeService.upsertBoxOffice(titleId, req));
    }

    @GetMapping("/api/v1/box-office/top")
    public ResponseEntity<List<BoxOfficeDTO>> getTopGrossing(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(boxOfficeService.getTopGrossing(limit));
    }

}
