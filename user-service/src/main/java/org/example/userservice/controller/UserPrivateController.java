package org.example.userservice.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.Request.UserRegisterRequest;
import org.example.sharedmodule.user_service.dto.UserDTO;
import org.example.sharedmodule.api_gateway.dto.CreateUserRequest;
import org.example.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/private/user")
@RequiredArgsConstructor
@Hidden
public class UserPrivateController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UUID> register(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/from-credential")
    public ResponseEntity<UUID> registerFromCredential(@RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam @NotBlank @Email String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping("/oauth")
    public ResponseEntity<UserDTO> findOrCreateOAuthUser(@RequestParam @NotBlank @Email String email) {
        return ResponseEntity.ok(userService.findOrCreateOAuthUser(email));
    }

}
