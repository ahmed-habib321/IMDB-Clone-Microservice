package org.example.newsservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "news_articles",
        indexes = {
                @Index(name = "idx_news_slug",       columnList = "slug", unique = true),
                @Index(name = "idx_news_published",  columnList = "published_at")
        })
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String authorUsername;
    private String title;
    private String slug;
    private String excerpt;
    private String body;
    private String coverUrl;
    private Integer viewCount;
    private Boolean isPublished;

    @CreatedDate
    private Instant createdAt;

    @CreatedDate
    private Instant publishedAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Transient
    private List<UUID> taggedTitleIds;
    @Transient
    private List<UUID> taggedPersonIds;
}