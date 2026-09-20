package org.example.apigateway.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.apigateway.dto.EditorRequestResponse;
import org.example.apigateway.service.EditorRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Editor Requests")
public class EditorRequestController {

    private final EditorRequestService editorRequestService;

    @PostMapping("/editor-request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EditorRequestResponse> requestEditorRole(@AuthenticationPrincipal String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(editorRequestService.requestEditorRole(UUID.fromString(userId)));
    }
}