package org.example.newsservice.repository;

import org.example.newsservice.model.NewsPeopleTag;
import org.example.newsservice.model.NewsPeopleTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NewsPeopleTagRepository extends JpaRepository<NewsPeopleTag, NewsPeopleTagId> {
    void deleteByIdArticleId(UUID articleId);
}