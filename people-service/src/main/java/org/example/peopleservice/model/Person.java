package org.example.peopleservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "people",
        indexes = {
                @Index(name = "idx_people_slug",   columnList = "slug", unique = true),
                @Index(name = "idx_people_popular", columnList = "popularity")
        })
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String slug;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> alsoKnownAs;
    private String biography;
    private String profileUrl;

    private LocalDate birthDate;
    private LocalDate deathDate;
    private String birthPlace;
    private String gender;

    private Integer heightCm;
    private Double popularity;

    private String imdbId;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @OneToMany(mappedBy = "person")
    private List<Cast> castedIn;

    @OneToMany(mappedBy = "person")
    private List<Crew> workedIn;
}
