package org.example.apigateway.service;

import org.example.apigateway.dto.EditorRequestResponse;
import org.example.apigateway.model.AuthCredential;
import org.example.apigateway.model.EditorRequest;
import org.example.apigateway.model.EditorRequestStatus;
import org.example.apigateway.model.Role;
import org.example.apigateway.repository.AuthCredentialRepository;
import org.example.apigateway.repository.EditorRequestRepository;
import org.example.sharedmodule.general_exceptions.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class EditorRequestServiceTest {

    @Mock EditorRequestRepository editorRequestRepository;
    @Mock AuthCredentialRepository authCredentialRepository;

    @InjectMocks
    EditorRequestService editorRequestService;

    private static final UUID USER_ID = UUID.randomUUID();

    private AuthCredential credential(Role role) {
        return AuthCredential.builder()
                .userId(USER_ID)
                .email("user@example.com")
                .passwordHash("hash")
                .role(role)
                .isActive(true)
                .isVerified(true)
                .build();
    }

    private EditorRequest request(EditorRequestStatus status) {
        return EditorRequest.builder()
                .id(UUID.randomUUID())
                .userId(USER_ID)
                .email("user@example.com")
                .status(status)
                .requestedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("creates a pending request for a regular user")
    void createsPendingRequest() {
        when(authCredentialRepository.findById(USER_ID)).thenReturn(Optional.of(credential(Role.USER)));
        when(editorRequestRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(editorRequestRepository.save(any(EditorRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        EditorRequestResponse response = editorRequestService.requestEditorRole(USER_ID);

        assertThat(response.status()).isEqualTo(EditorRequestStatus.PENDING);
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.email()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("rejects a duplicate pending request")
    void rejectsDuplicatePendingRequest() {
        when(authCredentialRepository.findById(USER_ID)).thenReturn(Optional.of(credential(Role.USER)));
        when(editorRequestRepository.findByUserId(USER_ID)).thenReturn(Optional.of(request(EditorRequestStatus.PENDING)));

        assertThatThrownBy(() -> editorRequestService.requestEditorRole(USER_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already have a pending editor request");
    }

    @Test
    @DisplayName("reopens a previously rejected request")
    void reopensRejectedRequest() {
        when(authCredentialRepository.findById(USER_ID)).thenReturn(Optional.of(credential(Role.USER)));
        when(editorRequestRepository.findByUserId(USER_ID)).thenReturn(Optional.of(request(EditorRequestStatus.REJECTED)));
        when(editorRequestRepository.save(any(EditorRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        EditorRequestResponse response = editorRequestService.requestEditorRole(USER_ID);

        assertThat(response.status()).isEqualTo(EditorRequestStatus.PENDING);
    }

    @Test
    @DisplayName("prevents an already-editor user from requesting")
    void blocksExistingEditor() {
        when(authCredentialRepository.findById(USER_ID)).thenReturn(Optional.of(credential(Role.EDITOR)));

        assertThatThrownBy(() -> editorRequestService.requestEditorRole(USER_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already have an editor role");
    }

    @Test
    @DisplayName("approves a pending request and promotes the user to EDITOR")
    void approvesAndPromotes() {
        EditorRequest pending = request(EditorRequestStatus.PENDING);
        AuthCredential user = credential(Role.USER);
        when(editorRequestRepository.findById(pending.getId())).thenReturn(Optional.of(pending));
        when(authCredentialRepository.findByUserId(USER_ID)).thenReturn(Optional.of(user));
        when(editorRequestRepository.save(any(EditorRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        EditorRequestResponse response = editorRequestService.approve(pending.getId());

        assertThat(response.status()).isEqualTo(EditorRequestStatus.APPROVED);
        assertThat(user.getRole()).isEqualTo(Role.EDITOR);
        assertThat(response.reviewedAt()).isNotNull();
    }

    @Test
    @DisplayName("rejects a pending request without touching the role")
    void rejectsWithoutPromoting() {
        EditorRequest pending = request(EditorRequestStatus.PENDING);
        when(editorRequestRepository.findById(pending.getId())).thenReturn(Optional.of(pending));
        when(editorRequestRepository.save(any(EditorRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        EditorRequestResponse response = editorRequestService.reject(pending.getId());

        assertThat(response.status()).isEqualTo(EditorRequestStatus.REJECTED);
        verify(authCredentialRepository, never()).save(any());
    }

    @Test
    @DisplayName("lists requests filtered by status")
    void listsRequests() {
        when(editorRequestRepository.findByStatus(EditorRequestStatus.PENDING))
                .thenReturn(List.of(request(EditorRequestStatus.PENDING), request(EditorRequestStatus.PENDING)));

        List<EditorRequestResponse> responses = editorRequestService.listRequests(EditorRequestStatus.PENDING);

        assertThat(responses).hasSize(2);
    }
}