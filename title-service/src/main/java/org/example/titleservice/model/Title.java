package org.example.titleservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.sharedmodule.title_service.enums.TitleStatus;
import org.example.sharedmodule.title_service.enums.TitleType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(indexes = {
                @Index(name = "idx_titles_slug", columnList = "slug", unique = true),
                @Index(name = "idx_titles_dtype", columnList = "dtype"),
                @Index(name = "idx_titles_popular", columnList = "popularity"),
                @Index(name = "idx_titles_release_date", columnList = "releaseDate")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
@EntityListeners(AuditingEntityListener.class)
public class Title {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TitleType titleType;
    private String primaryTitle;
    private String originalTitle;

    @Column(unique = true)
    private String slug;
    private String tagline;
    private String overview;
    private String posterUrl;
    private String backdropUrl;

    @Enumerated(EnumType.STRING)
    private TitleStatus status;
    private LocalDate releaseDate;
    private Integer runtimeMins;
    private Long budget;
    private Long revenue;
    private Double imdbRating;
    private Integer voteCount;
    private Double popularity;
    private Boolean adult;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    // -------- RELATIONS --------

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "title_genres", joinColumns = @JoinColumn(name = "title_id"))
    @Column(name = "genre")
    private Set<String> genres = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "title_languages", joinColumns = @JoinColumn(name = "title_id"))
    @Column(name = "language")
    private Set<String> languages = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "title_countries", joinColumns = @JoinColumn(name = "title_id"))
    @Column(name = "country")
    private Set<String> countries = new HashSet<>();

}
