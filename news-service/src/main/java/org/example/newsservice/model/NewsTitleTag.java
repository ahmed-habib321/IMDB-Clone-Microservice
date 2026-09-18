package org.example.newsservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "news_title_tags")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsTitleTag {

    @EmbeddedId
    private NewsTitleTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    private NewsArticle article;
}