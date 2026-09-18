package org.example.notificationservice.repository;

import org.example.notificationservice.entity.EmailJob;
import org.example.notificationservice.enums.EmailJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmailJobRepository extends JpaRepository<EmailJob, UUID> {

    List<EmailJob> findTop100ByStatusOrderByCreatedAtAsc(EmailJobStatus status);
}
