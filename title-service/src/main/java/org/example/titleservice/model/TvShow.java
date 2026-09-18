package org.example.titleservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("TvShow")
public class TvShow extends Title {

    private String network;
    private UUID creatorId;
    private Integer totalSeasons;
    private Integer totalEpisodes;
    private Integer episodeRuntime;
    private Boolean isOnGoing;

    @OneToMany(mappedBy = "tvShow", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Season> seasons;

    private LocalDate finishedAt;
}
