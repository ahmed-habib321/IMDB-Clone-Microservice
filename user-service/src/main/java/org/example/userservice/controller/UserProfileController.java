package org.example.userservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.userservice.dto.Response.UserProfileResponse;
import org.example.userservice.dto.Request.UpdateUserProfileRequest;
import org.example.userservice.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/{id}/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Manages user Profile.")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable String id) {
        return ResponseEntity.ok(userProfileService.getProfile(id));
    }

    @PutMapping
    public ResponseEntity<Void> updateProfile(@PathVariable String id, @Valid @RequestBody UpdateUserProfileRequest updateUserProfileRequest) {
        userProfileService.updateProfile(id, updateUserProfileRequest);
        return ResponseEntity.noContent().build();
    }

}
