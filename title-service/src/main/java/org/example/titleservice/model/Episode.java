package org.example.titleservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Episode {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "season_id")
    private Season season;
    private Integer episodeNumber;
    private String title;
    private String overview;
    private String stillUrl;
    private LocalDate airDate;
    private Integer runtimeMins;
    private Double imdbRating;
    private Integer voteCount;
    private UUID writerId;
    private UUID directorId;
}
