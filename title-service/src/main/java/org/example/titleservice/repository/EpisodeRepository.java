package org.example.titleservice.repository;

import org.example.titleservice.model.Episode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, UUID> {

    @Query("SELECT e FROM Episode e JOIN e.season s WHERE s.tvShow.id = :tvShowId AND s.seasonNumber = :seasonNumber ORDER BY e.episodeNumber ASC")
    Page<Episode> findEpisodesByShowAndSeason(@Param("tvShowId") UUID tvShowId, @Param("seasonNumber") int seasonNumber, Pageable pageable);

    @Query("SELECT e FROM Episode e JOIN e.season s WHERE s.tvShow.id    = :tvShowId AND s.seasonNumber = :seasonNumber AND e.episodeNumber = :episodeNumber")
    Optional<Episode> findEpisode(@Param("tvShowId") UUID tvShowId, @Param("seasonNumber") int seasonNumber, @Param("episodeNumber") int episodeNumber);
}
