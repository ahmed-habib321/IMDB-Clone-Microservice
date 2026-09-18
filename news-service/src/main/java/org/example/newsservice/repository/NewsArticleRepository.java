package org.example.newsservice.repository;

import org.example.newsservice.model.NewsArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, UUID> {

    Optional<NewsArticle> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<NewsArticle> findByIsPublishedTrueOrderByPublishedAtDesc(Pageable pageable);

    @Query("""
            SELECT a FROM NewsTitleTag t JOIN t.article a
            WHERE t.id.titleId = :titleId AND a.isPublished = true
            ORDER BY a.publishedAt DESC
            """)
    Page<NewsArticle> findByTitleTag(@Param("titleId") UUID titleId, Pageable pageable);

    @Query("""
            SELECT a FROM NewsPeopleTag t JOIN t.article a
            WHERE t.id.personId = :personId AND a.isPublished = true
            ORDER BY a.publishedAt DESC
            """)
    Page<NewsArticle> findByPersonTag(@Param("personId") UUID personId, Pageable pageable);

    @Modifying
    @Query("UPDATE NewsArticle a SET a.viewCount = a.viewCount + 1 WHERE a.id = :id")
    void incrementViewCount(@Param("id") UUID id);
}