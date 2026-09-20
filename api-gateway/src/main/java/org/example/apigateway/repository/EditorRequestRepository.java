package org.example.apigateway.repository;

import org.example.apigateway.model.EditorRequest;
import org.example.apigateway.model.EditorRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EditorRequestRepository extends JpaRepository<EditorRequest, UUID> {

    Optional<EditorRequest> findByUserId(UUID userId);

    List<EditorRequest> findByStatus(EditorRequestStatus status);
}