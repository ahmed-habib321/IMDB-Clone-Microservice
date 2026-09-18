package org.example.newsservice.repository;

import org.example.newsservice.model.NewsTitleTag;
import org.example.newsservice.model.NewsTitleTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NewsTitleTagRepository extends JpaRepository<NewsTitleTag, NewsTitleTagId> {
    void deleteByIdArticleId(UUID articleId);
}