package org.example.peopleservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.peopleservice.dto.AddCrewRequest;
import org.example.peopleservice.dto.CrewResponse;
import org.example.peopleservice.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/crew")
@RequiredArgsConstructor
@Tag(name = "Crew", description = "Crew credits for titles")
public class CrewController {

    private final PersonService personService;


    @GetMapping("/{titleId}")
    public ResponseEntity<List<CrewResponse>> getCrew(@PathVariable String titleId) {
        return ResponseEntity.ok(personService.getCrew(titleId));
    }

    @PostMapping("/{titleId}")
    public ResponseEntity<CrewResponse> addCrew(@PathVariable String titleId, @Valid @RequestBody AddCrewRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personService.addCrewMember(titleId, req));
    }
}
