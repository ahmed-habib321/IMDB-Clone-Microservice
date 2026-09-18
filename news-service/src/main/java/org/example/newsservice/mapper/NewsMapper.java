package org.example.newsservice.mapper;

import org.example.newsservice.dto.CreateNewsArticleRequest;
import org.example.newsservice.dto.NewsDetailResponse;
import org.example.newsservice.dto.NewsResponse;
import org.example.newsservice.dto.UpdateNewsRequest;
import org.example.newsservice.model.NewsArticle;
import org.example.newsservice.model.NewsPeopleTag;
import org.example.newsservice.model.NewsPeopleTagId;
import org.example.newsservice.model.NewsTitleTag;
import org.example.newsservice.model.NewsTitleTagId;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface NewsMapper {

    default NewsArticle create(CreateNewsArticleRequest req, String slug) {
        return NewsArticle.builder()
                .authorUsername(req.authorUsername())
                .title(req.title())
                .slug(slug)
                .body(req.body())
                .viewCount(0)
                .isPublished(true)
                .build();
    }

    default NewsTitleTag toTitleTag(NewsArticle article, UUID titleId) {
        return NewsTitleTag.builder()
                .id(new NewsTitleTagId(article.getId(), titleId))
                .article(article)
                .build();
    }

    default NewsPeopleTag toPeopleTag(NewsArticle article, UUID personId) {
        return NewsPeopleTag.builder()
                .id(new NewsPeopleTagId(article.getId(), personId))
                .article(article)
                .build();
    }

    NewsResponse toResponse(NewsArticle article);

    NewsDetailResponse toDetailResponse(NewsArticle article);

    @org.mapstruct.Mapping(target = "id",              ignore = true)
    @org.mapstruct.Mapping(target = "authorUsername",  ignore = true)
    @org.mapstruct.Mapping(target = "slug",            ignore = true)
    @org.mapstruct.Mapping(target = "viewCount",       ignore = true)
    @org.mapstruct.Mapping(target = "isPublished",     ignore = true)
    @org.mapstruct.Mapping(target = "createdAt",       ignore = true)
    @org.mapstruct.Mapping(target = "publishedAt",     ignore = true)
    @org.mapstruct.Mapping(target = "updatedAt",       ignore = true)
    @org.mapstruct.Mapping(target = "taggedTitleIds",  ignore = true)
    @org.mapstruct.Mapping(target = "taggedPersonIds", ignore = true)
    void updateFromRequest(UpdateNewsRequest req, @MappingTarget NewsArticle article);

}