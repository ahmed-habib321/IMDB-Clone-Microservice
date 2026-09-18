package org.example.titleservice.repository;

import org.example.titleservice.model.Movie;
import org.example.titleservice.model.Title;
import org.example.titleservice.model.TvShow;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TitleRepository extends JpaRepository<Title, UUID> {

    @EntityGraph(attributePaths = {"genres", "languages", "countries"})
    @Query("SELECT t FROM Title t WHERE t.id = :id AND TYPE(t) = Movie")
    Optional<Movie> findMovieByIdWithDetails(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"genres", "languages", "countries", "seasons", "seasons.episodes"})
    @Query("SELECT t FROM Title t WHERE t.id = :id AND TYPE(t) = TvShow")
    Optional<TvShow> findShowByIdWithDetails(@Param("id") UUID id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Title t SET t.imdbRating = :rating, t.voteCount = :count WHERE t.id = :titleId")
    void updateRatingStats(@Param("titleId") UUID titleId, @Param("rating") Double rating, @Param("count") Integer count);

    @EntityGraph(attributePaths = {"genres"})
    @Query("SELECT DISTINCT t FROM Title t ORDER BY t.popularity DESC")
    List<Title> findTrendingTitles(Pageable pageable);

    @EntityGraph(attributePaths = {"genres"})
    @Query("SELECT DISTINCT t FROM Title t WHERE t.status = 'RELEASED' ORDER BY t.popularity DESC")
    List<Title> findFeaturedTitles(Pageable pageable);

    @EntityGraph(attributePaths = {"genres"})
    @Query("SELECT DISTINCT t FROM Title t ORDER BY t.releaseDate DESC")
    List<Title> findNewReleases(Pageable pageable);

    @Query("SELECT DISTINCT g FROM Title t JOIN t.genres g ORDER BY g")
    List<String> findDistinctGenres();

    boolean existsBySlug(String slug);
}
