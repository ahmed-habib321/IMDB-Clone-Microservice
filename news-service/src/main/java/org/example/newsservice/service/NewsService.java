package org.example.newsservice.service;

import lombok.RequiredArgsConstructor;
import org.example.newsservice.dto.CreateNewsArticleRequest;
import org.example.newsservice.dto.NewsDetailResponse;
import org.example.newsservice.dto.NewsResponse;
import org.example.newsservice.dto.UpdateNewsRequest;
import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.example.newsservice.mapper.NewsMapper;
import org.example.newsservice.model.*;
import org.example.newsservice.repository.NewsArticleRepository;
import org.example.newsservice.repository.NewsPeopleTagRepository;
import org.example.newsservice.repository.NewsTitleTagRepository;
import org.example.sharedmodule.utils.SlugUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsArticleRepository articleRepository;
    private final NewsTitleTagRepository titleTagRepository;
    private final NewsPeopleTagRepository peopleTagRepository;
    private final NewsMapper newsMapper;

    // ────────────────────────────────────────────────
    //  READ
    // ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<NewsResponse> getNews(Pageable pageable) {
        return articleRepository.findByIsPublishedTrueOrderByPublishedAtDesc(pageable).map(newsMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<NewsResponse> getByTitleTag(UUID titleId, Pageable pageable) {
        return articleRepository.findByTitleTag(titleId, pageable).map(newsMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<NewsResponse> getByPersonTag(UUID personId, Pageable pageable) {
        return articleRepository.findByPersonTag(personId, pageable).map(newsMapper::toResponse);
    }

    @Transactional
    public NewsDetailResponse getBySlug(String slug) {
        NewsArticle article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + slug));
        articleRepository.incrementViewCount(article.getId());
        return newsMapper.toDetailResponse(article);
    }

    // ────────────────────────────────────────────────
    //  CREATE
    // ────────────────────────────────────────────────

    @Transactional
    public NewsResponse create(CreateNewsArticleRequest req) {
        String slug = SlugUtils.generateUniqueSlug(req.title(), articleRepository::existsBySlug);

        NewsArticle saved = articleRepository.save(newsMapper.create(req, slug));

        // Apply tags if provided
        if (req.taggedTitleIds() != null && !req.taggedTitleIds().isEmpty())
            applyTitleTags(saved, req.taggedTitleIds());
        if (req.taggedPersonIds() != null && !req.taggedPersonIds().isEmpty())
            applyPeopleTags(saved, req.taggedPersonIds());

        return newsMapper.toResponse(saved);
    }

    // ────────────────────────────────────────────────
    //  UPDATE / DELETE
    // ────────────────────────────────────────────────

    @Transactional
    public NewsResponse update(String slug, UpdateNewsRequest req) {
        NewsArticle article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + slug));

        String oldTitle = article.getTitle();
        newsMapper.updateFromRequest(req, article);

        if (req.title() != null && !req.title().equals(oldTitle)) {
            article.setSlug(SlugUtils.generateUniqueSlug(req.title(), articleRepository::existsBySlug));
        }

        articleRepository.save(article);

        if (req.taggedTitleIds() != null) {
            titleTagRepository.deleteByIdArticleId(article.getId());
            if (!req.taggedTitleIds().isEmpty())
                applyTitleTags(article, req.taggedTitleIds());
        }
        if (req.taggedPersonIds() != null) {
            peopleTagRepository.deleteByIdArticleId(article.getId());
            if (!req.taggedPersonIds().isEmpty())
                applyPeopleTags(article, req.taggedPersonIds());
        }

        return newsMapper.toResponse(article);
    }

    @Transactional
    public void delete(String slug) {
        NewsArticle article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + slug));
        titleTagRepository.deleteByIdArticleId(article.getId());
        peopleTagRepository.deleteByIdArticleId(article.getId());
        articleRepository.delete(article);
    }

    // ────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ────────────────────────────────────────────────

    private void applyTitleTags(NewsArticle article, List<UUID> titleIds) {
        titleTagRepository.saveAll(titleIds.stream()
                .map(titleId -> newsMapper.toTitleTag(article, titleId))
                .toList());
    }

    private void applyPeopleTags(NewsArticle article, List<UUID> personIds) {
        peopleTagRepository.saveAll(personIds.stream()
                .map(personId -> newsMapper.toPeopleTag(article, personId))
                .toList());
    }

}