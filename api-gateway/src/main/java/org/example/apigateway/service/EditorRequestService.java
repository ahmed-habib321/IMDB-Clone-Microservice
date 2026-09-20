package org.example.apigateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.apigateway.dto.EditorRequestResponse;
import org.example.apigateway.model.AuthCredential;
import org.example.apigateway.model.EditorRequest;
import org.example.apigateway.model.EditorRequestStatus;
import org.example.apigateway.model.Role;
import org.example.apigateway.repository.AuthCredentialRepository;
import org.example.apigateway.repository.EditorRequestRepository;
import org.example.sharedmodule.general_exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EditorRequestService {

    private final EditorRequestRepository editorRequestRepository;
    private final AuthCredentialRepository authCredentialRepository;

    @Transactional
    public EditorRequestResponse requestEditorRole(UUID userId) {
        AuthCredential credential = authCredentialRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        if (credential.getRole() == Role.EDITOR || credential.getRole() == Role.ADMIN) {
            throw new BusinessException("You already have an editor role", HttpStatus.CONFLICT);
        }

        EditorRequest existing = editorRequestRepository.findByUserId(userId).orElse(null);
        if (existing != null && existing.getStatus() == EditorRequestStatus.PENDING) {
            throw new BusinessException("You already have a pending editor request", HttpStatus.CONFLICT);
        }

        EditorRequest request;
        if (existing != null) {
            existing.setStatus(EditorRequestStatus.PENDING);
            existing.setRequestedAt(Instant.now());
            existing.setReviewedAt(null);
            request = existing;
        } else {
            request = EditorRequest.builder()
                    .userId(userId)
                    .email(credential.getEmail())
                    .status(EditorRequestStatus.PENDING)
                    .requestedAt(Instant.now())
                    .build();
        }

        EditorRequest saved = editorRequestRepository.save(request);
        log.info("Editor request created for userId {}", userId);
        return map(saved);
    }

    @Transactional(readOnly = true)
    public List<EditorRequestResponse> listRequests(EditorRequestStatus status) {
        List<EditorRequest> requests = status != null
                ? editorRequestRepository.findByStatus(status)
                : editorRequestRepository.findAll();
        return requests.stream()
                .sorted(Comparator.comparing(EditorRequest::getRequestedAt).reversed())
                .map(this::map)
                .toList();
    }

    @Transactional
    public EditorRequestResponse approve(UUID requestId) {
        EditorRequest request = getPending(requestId);

        AuthCredential credential = authCredentialRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new BusinessException("User for this request no longer exists", HttpStatus.NOT_FOUND));

        credential.setRole(Role.EDITOR);
        authCredentialRepository.save(credential);

        request.setStatus(EditorRequestStatus.APPROVED);
        request.setReviewedAt(Instant.now());
        EditorRequest saved = editorRequestRepository.save(request);
        log.info("Editor request {} approved for user {}", requestId, request.getUserId());
        return map(saved);
    }

    @Transactional
    public EditorRequestResponse reject(UUID requestId) {
        EditorRequest request = getPending(requestId);

        request.setStatus(EditorRequestStatus.REJECTED);
        request.setReviewedAt(Instant.now());
        EditorRequest saved = editorRequestRepository.save(request);
        log.info("Editor request {} rejected for user {}", requestId, request.getUserId());
        return map(saved);
    }

    private EditorRequest getPending(UUID requestId) {
        EditorRequest request = editorRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException("Editor request not found", HttpStatus.NOT_FOUND));
        if (request.getStatus() != EditorRequestStatus.PENDING) {
            throw new BusinessException("Editor request is not pending", HttpStatus.BAD_REQUEST);
        }
        return request;
    }

    private EditorRequestResponse map(EditorRequest request) {
        return new EditorRequestResponse(
                request.getId(),
                request.getUserId(),
                request.getEmail(),
                request.getStatus(),
                request.getRequestedAt(),
                request.getReviewedAt()
        );
    }
}