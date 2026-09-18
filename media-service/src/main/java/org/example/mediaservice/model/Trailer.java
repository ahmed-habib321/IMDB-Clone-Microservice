package org.example.mediaservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trailers",
        indexes = @Index(name = "idx_trailers_title", columnList = "title_id"))
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Trailer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Scalar UUID — Title lives in title-service
    @Column(name = "title_id", nullable = false)
    private UUID titleId;

    private String name;
    private String trailerType;
    private String youtubeKey;   // ✅ renamed from youtubeUrl to match DTO
    private Integer durationSecs;
    private String language;
    private Instant publishedAt;
}