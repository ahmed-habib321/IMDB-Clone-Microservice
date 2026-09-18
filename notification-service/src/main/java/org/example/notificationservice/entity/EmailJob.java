package org.example.notificationservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.notificationservice.enums.EmailJobStatus;
import org.example.notificationservice.enums.NotificationTemplate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "email_jobs",
    indexes = {
        @Index(name = "idx_email_job_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class EmailJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "message_id", nullable = false, unique = true)
    private UUID messageId;

    @Column(nullable = false)
    private String recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationTemplate template;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String variablesJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EmailJobStatus status = EmailJobStatus.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private int retryCount = 0;

    private LocalDateTime lastAttemptAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
