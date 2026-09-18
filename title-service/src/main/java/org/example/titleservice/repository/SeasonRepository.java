package org.example.titleservice.repository;

import org.example.titleservice.model.Season;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface SeasonRepository extends JpaRepository<Season, UUID> {

    @Query("SELECT s FROM Season s WHERE s.tvShow.id = :tvShowId ORDER BY s.seasonNumber ASC")
    Page<Season> findByTvShowId(@Param("tvShowId") UUID tvShowId, Pageable pageable);

    @EntityGraph(attributePaths = "episodes")
    @Query("SELECT s FROM Season s WHERE s.id IN :ids")
    List<Season> findByIdsWithEpisodes(@Param("ids") Collection<UUID> ids);
}
