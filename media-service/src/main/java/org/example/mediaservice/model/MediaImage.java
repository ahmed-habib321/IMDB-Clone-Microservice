package org.example.mediaservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.mediaservice.model.enums.ImageType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "media_images",
        indexes = {
                @Index(name = "idx_media_images_title",  columnList = "title_id"),
                @Index(name = "idx_media_images_person", columnList = "person_id")
        })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MediaImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Scalar UUIDs — owners live in their respective services
    @Column(name = "title_id")
    private UUID titleId;

    @Column(name = "person_id")
    private UUID personId;

    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    private String url;
    private Integer width;
    private Integer height;
    private Boolean isPrimary;
    private UUID uploadedBy;

    @CreatedDate
    private Instant uploadedAt;
}
