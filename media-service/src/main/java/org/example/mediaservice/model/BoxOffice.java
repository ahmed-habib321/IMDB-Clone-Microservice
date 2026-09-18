package org.example.mediaservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "box_office",
        uniqueConstraints = @UniqueConstraint(columnNames = {"title_id"}))
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class BoxOffice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title_id", nullable = false, unique = true)
    private UUID titleId;

    private Long budget;
    @Column(name = "opening_wknd")
    private Long openingWeekend;
    private Long domestic;
    private Long international;
    private Long worldwide;
    private String currency;
    private String source;
}