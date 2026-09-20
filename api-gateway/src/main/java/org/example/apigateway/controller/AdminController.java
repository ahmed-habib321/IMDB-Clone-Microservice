package org.example.apigateway.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.apigateway.dto.EditorRequestResponse;
import org.example.apigateway.model.EditorRequestStatus;
import org.example.apigateway.service.EditorRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
public class AdminController {

    private final EditorRequestService editorRequestService;

    @GetMapping("/editor-requests")
    public ResponseEntity<List<EditorRequestResponse>> listEditorRequests(
            @RequestParam(required = false) EditorRequestStatus status) {
        return ResponseEntity.ok(editorRequestService.listRequests(status));
    }

    @PostMapping("/editor-requests/{requestId}/approve")
    public ResponseEntity<EditorRequestResponse> approveEditorRequest(@PathVariable UUID requestId) {
        return ResponseEntity.ok(editorRequestService.approve(requestId));
    }

    @PostMapping("/editor-requests/{requestId}/reject")
    public ResponseEntity<EditorRequestResponse> rejectEditorRequest(@PathVariable UUID requestId) {
        return ResponseEntity.ok(editorRequestService.reject(requestId));
    }
}