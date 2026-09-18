package org.example.ratingsreviewsservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reviews",
        indexes = {
                @Index(name = "idx_reviews_title", columnList = "title_id"),
                @Index(name = "idx_reviews_user",  columnList = "user_id")
        })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title_id", nullable = false)
    private UUID titleId;

    private String reviewTitle;
    private String body;
    private Boolean containsSpoiler;
    private Boolean isApproved;
    private Integer helpfulYes;
    private Integer helpfulNo;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
