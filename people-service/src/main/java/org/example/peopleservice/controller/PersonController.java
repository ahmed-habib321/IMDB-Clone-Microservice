package org.example.peopleservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.peopleservice.dto.*;
import org.example.peopleservice.service.PersonService;
import org.example.sharedmodule.title_service.dto.TitleMiniResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/people")
@RequiredArgsConstructor
@Tag(name = "People", description = "Actors, directors, writers, and crew")
public class PersonController {

    private final PersonService personService;


    @GetMapping
    public ResponseEntity<Page<PersonResponse>> getPeople(@ModelAttribute PersonSearchQuery filter, Pageable pageable) {
        return ResponseEntity.ok(personService.searchPeople(filter, pageable));
    }

    @PostMapping
    public ResponseEntity<PersonResponse> createPerson(@Valid @RequestBody CreatePersonRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personService.createPerson(req));
    }


    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> getPerson(@PathVariable String id) {
        return ResponseEntity.ok(personService.getPerson(id));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<PersonDetailResponse> getPersonDetail(@PathVariable String id) {
        return ResponseEntity.ok(personService.getPersonDetail(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<PersonDetailResponse> getPersonBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(personService.getPersonBySlug(slug));
    }


    @PutMapping("/{id}")
    public ResponseEntity<PersonResponse> updatePerson(@PathVariable String id, @Valid @RequestBody UpdatePersonRequest req) {
        return ResponseEntity.ok(personService.updatePerson(id, req));
    }

    @GetMapping("/{id}/filmography")
    public ResponseEntity<Page<TitleMiniResponse>> getFilmography(@PathVariable UUID id, Pageable pageable) {
        return ResponseEntity.ok(personService.getFilmography(id, pageable));
    }

}
