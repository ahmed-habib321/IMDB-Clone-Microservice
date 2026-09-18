package org.example.awardsservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.awardsservice.model.enums.AwardOutcome;

import java.util.UUID;

@Entity
@Table(name = "award_nominations",
        indexes = {
                @Index(name = "idx_nominations_title",  columnList = "title_id"),
                @Index(name = "idx_nominations_person", columnList = "person_id"),
                @Index(name = "idx_nominations_year",   columnList = "year")
        })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AwardNomination {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "award_id", nullable = false)
    private Award award;

    @Column(name = "title_id")
    private UUID titleId;

    @Column(name = "person_id")
    private UUID personId;

    private String titleName;
    private String personName;

    private String category;
    private Short year;

    @Enumerated(EnumType.STRING)
    private AwardOutcome outcome;

    @Column(columnDefinition = "TEXT")
    private String notes;
}