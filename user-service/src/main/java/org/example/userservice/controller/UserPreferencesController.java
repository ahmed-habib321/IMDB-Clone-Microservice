package org.example.userservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.Request.UpdateUserPreferencesRequest;
import org.example.userservice.dto.Response.UserPreferencesResponse;
import org.example.userservice.service.UserPreferencesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/{id}/preferences")
@RequiredArgsConstructor
@Tag(name = "Preferences", description = "Manages user Preferences.")
public class UserPreferencesController {

    private final UserPreferencesService userPreferencesService;

    @GetMapping
    public ResponseEntity<UserPreferencesResponse> getPreferences(@PathVariable String id) {
        return ResponseEntity.ok(userPreferencesService.getPreference(id));
    }

    @PutMapping
    public ResponseEntity<Void> updatePreferences(@PathVariable String id, @Valid @RequestBody UpdateUserPreferencesRequest updateUserPreferencesRequest) {
        userPreferencesService.updatePreferences(id, updateUserPreferencesRequest);
        return ResponseEntity.noContent().build();
    }


}
