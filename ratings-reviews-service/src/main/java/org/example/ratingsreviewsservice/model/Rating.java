package org.example.ratingsreviewsservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ratings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "title_id"}),
        indexes = {
                @Index(name = "idx_ratings_title", columnList = "title_id"),
                @Index(name = "idx_ratings_user",  columnList = "user_id")
        })
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title_id", nullable = false)
    private UUID titleId;

    private Short score;

    @CreatedDate
    private Instant ratedAt;

    @LastModifiedDate
    private Instant updatedAt;
}