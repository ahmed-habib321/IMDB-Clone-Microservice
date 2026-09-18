package org.example.peopleservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.peopleservice.dto.AddCastRequest;
import org.example.peopleservice.dto.CastResponse;
import org.example.peopleservice.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cast")
@RequiredArgsConstructor
@Tag(name = "Cast", description = "Cast credits for titles")
public class CastController {


    private final PersonService personService;

    @GetMapping("/{titleId}")
    public ResponseEntity<List<CastResponse>> getCast(@PathVariable String titleId) {
        return ResponseEntity.ok(personService.getCast(titleId));
    }

    @PostMapping("/{titleId}")
    public ResponseEntity<CastResponse> addCast(@PathVariable String titleId, @Valid @RequestBody AddCastRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(personService.addCastMember(titleId, req));
    }




}
