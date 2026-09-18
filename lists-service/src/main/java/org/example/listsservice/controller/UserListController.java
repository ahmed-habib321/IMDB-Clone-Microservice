package org.example.listsservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.listsservice.dto.*;
import org.example.listsservice.service.UserListService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lists")
@RequiredArgsConstructor
@Tag(name = "User Lists", description = "Custom user lists")
public class UserListController {

    private final UserListService userListService;

    @GetMapping
    public ResponseEntity<Page<UserListResponse>> getLists(@RequestHeader("X-User-Id") UUID userId, Pageable pageable) {
        return ResponseEntity.ok(userListService.getLists(userId, pageable));
    }

    @GetMapping("/summary")
    public ResponseEntity<Page<ListResponse>> getUserLists(@RequestHeader("X-User-Id") UUID userId, Pageable pageable) {
        return ResponseEntity.ok(userListService.getUserLists(userId, pageable));
    }

    @PostMapping
    public ResponseEntity<UserListResponse> createList(@RequestHeader("X-User-Id") UUID userId, @Valid @RequestBody CreateListRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userListService.createList(userId, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserListDetailResponse> getList(@PathVariable UUID id) {
        return ResponseEntity.ok(userListService.getList(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserListResponse> updateList(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId, @Valid @RequestBody UpdateListRequest req) {
        return ResponseEntity.ok(userListService.updateList(id, userId, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        userListService.deleteList(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> addItem(@PathVariable UUID id, @RequestBody UUID titleId, @RequestHeader("X-User-Id") UUID userId) {
        userListService.addItem(id, titleId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/{titleId}")
    public ResponseEntity<Void> removeItem(@PathVariable UUID id, @PathVariable UUID titleId, @RequestHeader("X-User-Id") UUID userId) {
        userListService.removeItem(id, titleId, userId);
        return ResponseEntity.noContent().build();
    }
}