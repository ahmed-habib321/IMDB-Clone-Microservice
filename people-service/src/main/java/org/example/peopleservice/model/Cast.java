package org.example.peopleservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "cast",
        uniqueConstraints = @UniqueConstraint(columnNames = {"title_id", "person_id", "character_name"}))
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cast {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title_id", nullable = false)
    private UUID titleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    private String characterName;
    private Integer billingOrder;
    private Boolean isVoice;
    private Integer episodeCount;
}
