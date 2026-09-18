package org.example.titleservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.outbox.service.OutboxWriter;
import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.example.sharedmodule.title_service.event.TitleUpdatedEvent;
import org.example.sharedmodule.title_service.exception.TitleNotFoundException;
import org.example.sharedmodule.utils.SlugUtils;
import org.example.sharedmodule.utils.UUIDUtils;
import org.example.sharedmodule.title_service.dto.TitleMiniResponse;
import org.example.titleservice.dto.CreateTitle.CreateMovieRequest;
import org.example.titleservice.dto.CreateTitle.CreateTvShowRequest;
import org.example.titleservice.dto.Responses.*;
import org.example.titleservice.dto.UpdateTitle.UpdateMovieRequest;
import org.example.titleservice.dto.UpdateTitle.UpdateTvShowRequest;
import org.example.titleservice.mapper.TitleMapper;
import org.example.titleservice.model.*;
import org.example.titleservice.repository.EpisodeRepository;
import org.example.titleservice.repository.SeasonRepository;
import org.example.titleservice.repository.TitleRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.example.sharedmodule.Constants.TOPIC_NAMES.TITLE_INDEXED;
import static org.example.sharedmodule.Constants.TOPIC_NAMES.TITLE_UPDATED;

@Service
@RequiredArgsConstructor
@Slf4j
public class TitleService {


    private final TitleRepository titleRepository;
    private final TitleMapper titleMapper;
    private final SeasonRepository seasonRepository;
    private final EpisodeRepository episodeRepository;
    private final OutboxWriter outboxWriter;


    // ────────────────────────────────────────────────
    //  CREATE
    // ────────────────────────────────────────────────

    @Transactional
    public void createMovie(CreateMovieRequest req) {
        Movie movie = titleMapper.createMovie(req);

        movie.setSlug(SlugUtils.generateUniqueSlug(req.primaryTitle(), titleRepository::existsBySlug));
        Movie saved = (Movie) saveWithSlugRetry(movie);

//        Movie saved = titleRepository.save(movie);
        publishCreated(saved);
    }

    @Transactional
    public void createShow(CreateTvShowRequest req) {
        TvShow show = titleMapper.createShow(req);

        show.setSlug(SlugUtils.generateUniqueSlug(req.primaryTitle(), titleRepository::existsBySlug));

        // TODO make Mapper handel it
        if (req.seasons() != null && !req.seasons().isEmpty()) show.setSeasons(titleMapper.toSeasons(req.seasons(), show));

        TvShow saved = (TvShow) saveWithSlugRetry(show);

        publishCreated(saved);
    }

    // ────────────────────────────────────────────────
    //  READ
    // ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    @Cacheable(value = "movies",key = "#id")
    public MovieFullResponse getMovie(String id) {
        UUID uuid = UUIDUtils.parse(id);
        Movie movie = titleRepository.findMovieByIdWithDetails(uuid).orElseThrow(() -> new TitleNotFoundException(uuid));
        return titleMapper.toMovieFullResponse(movie);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "shows",key = "#id")
    public ShowFullResponse getShow(String id) {
        UUID uuid = UUIDUtils.parse(id);
        TvShow show = titleRepository.findShowByIdWithDetails(uuid).orElseThrow(() -> new TitleNotFoundException(uuid));
        return titleMapper.toShowFullResponse(show);
    }

    @Transactional(readOnly = true)
    public List<TitleMiniResponse> getTitlesForSearch(List<String> ids) {
        List<UUID> uuids = ids.stream().map(UUIDUtils::parse).toList();


        Map<UUID, Title> titleMap = titleRepository.findAllById(uuids)
                .stream()
                .collect(Collectors.toMap(Title::getId, Function.identity()));

        return uuids.stream()
                .map(titleMap::get)
                .filter(Objects::nonNull)
                .map(titleMapper::toTitleMiniResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TitleCardResponse> getTrendingTitles() {
        return titleRepository.findTrendingTitles(PageRequest.of(0, 10))
                .stream()
                .map(titleMapper::toTitleCardResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TitleCardResponse> getFeaturedTitles() {
        return titleRepository.findFeaturedTitles(PageRequest.of(0, 8))
                .stream()
                .map(titleMapper::toTitleCardResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TitleCardResponse> getNewReleases() {
        return titleRepository.findNewReleases(PageRequest.of(0, 12))
                .stream()
                .map(titleMapper::toTitleCardResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> getGenres() {
        return titleRepository.findDistinctGenres();
    }

    // ────────────────────────────────────────────────
    //  UPDATE
    // ────────────────────────────────────────────────

    @Transactional
    @CachePut(value = "movies",key = "#id")
    public MovieFullResponse updateMovie(String id, UpdateMovieRequest req) {
        UUID uuid = UUIDUtils.parse(id);
        Movie movie = titleRepository.findMovieByIdWithDetails(uuid).orElseThrow(() -> new TitleNotFoundException(uuid));
        String currentTitle = movie.getPrimaryTitle();
        titleMapper.updateMovieFromRequest(req, movie);
        if (req.primaryTitle() != null && !req.primaryTitle().equals(currentTitle)) {
            movie.setSlug(SlugUtils.generateUniqueSlug(movie.getPrimaryTitle(), titleRepository::existsBySlug));
        }
        publishUpdated(titleRepository.save(movie));
        return titleMapper.toMovieFullResponse(movie);
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "shows", key = "#id"),
            evict = {
                    @CacheEvict(value = "seasons", key = "#id"),
                    @CacheEvict(value = "episodes", key = "#id"),
                    @CacheEvict(value = "episode", key = "#id")
            }
    )
    public ShowFullResponse updateShow(String id, UpdateTvShowRequest req) {
        UUID uuid = UUIDUtils.parse(id);
        TvShow show = titleRepository.findShowByIdWithDetails(uuid).orElseThrow(() -> new TitleNotFoundException(uuid));
        String currentTitle = show.getPrimaryTitle();
        titleMapper.updateShowFromRequest(req, show);
        if (req.primaryTitle() != null && !req.primaryTitle().equals(currentTitle)) {
            show.setSlug(SlugUtils.generateUniqueSlug(show.getPrimaryTitle(), titleRepository::existsBySlug));
        }

        publishUpdated(titleRepository.save(show));
        return titleMapper.toShowFullResponse(show);
    }

    // ────────────────────────────────────────────────
    //  DELETE
    // ────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = {"movies","shows","seasons","episodes","episode"} ,key = "#id")
    public void deleteTitle(String id) {
        UUID uuid = UUIDUtils.parse(id);
        Title title = titleRepository.findById(uuid).orElseThrow(() -> new TitleNotFoundException(uuid));
        titleRepository.delete(title);
//        remove cache
    }

    // ────────────────────────────────────────────────
    //  SEASONS & EPISODES
    // ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    @Cacheable(value = "seasons",key = "#id")
    public Page<SeasonFullResponse> getSeasons(String id, Pageable pageable) {
        UUID uuid = UUIDUtils.parse(id);
        Title title = titleRepository.findById(uuid).orElseThrow(() -> new TitleNotFoundException(uuid));
        if (!(title instanceof TvShow)) throw new IllegalArgumentException("Title " + id + " is not a TV show");

        Page<Season> seasons = seasonRepository.findByTvShowId(uuid, pageable);
        List<UUID> seasonIds = seasons.getContent().stream().map(Season::getId).toList();
        if (!seasonIds.isEmpty()) seasonRepository.findByIdsWithEpisodes(seasonIds);
        //Cache the result
        return seasons.map(titleMapper::toSeasonFullResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "episodes",key = "#id")
    public Page<EpisodeFullResponse> getEpisodes(String id, short seasonNumber, Pageable pageable) {
        UUID uuid = UUIDUtils.parse(id);

        Title title = titleRepository.findById(uuid).orElseThrow(() -> new TitleNotFoundException(uuid));
        if (!(title instanceof TvShow)) throw new IllegalArgumentException("Title " + id + " is not a TV show");

        Page<Episode> episodes = episodeRepository.findEpisodesByShowAndSeason(uuid,seasonNumber,pageable);
        //Cache the result
        return episodes.map(titleMapper::toEpisodeFullResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "episode",key = "#id")
    public EpisodeFullResponse getEpisode(String id, short seasonNumber, short episodeNumber) {
        UUID uuid = UUIDUtils.parse(id);

        Episode episode = episodeRepository.findEpisode(uuid, seasonNumber, episodeNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Episode", "S" + seasonNumber + "EP" + episodeNumber));

        //Cache the result
        return titleMapper.toEpisodeFullResponse(episode);
    }

    // ────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ────────────────────────────────────────────────

    private void publishCreated(Title saved) {
        outboxWriter.save(titleMapper.toIndexEvent(saved), TITLE_INDEXED, saved.getId().toString());
    }

    private void publishUpdated(Title updated) {
        outboxWriter.save(new TitleUpdatedEvent(updated.getId(), updated.getSlug(), Instant.now()), TITLE_UPDATED, updated.getId().toString());
        outboxWriter.save(titleMapper.toIndexEvent(updated), TITLE_INDEXED, updated.getId().toString());
    }

    private Title saveWithSlugRetry(Title title) {
        try {
            return titleRepository.save(title);
        } catch (DataIntegrityViolationException ex) {
            String retrySlug = SlugUtils.slugify(title.getPrimaryTitle())
                    + "-" + UUID.randomUUID().toString().substring(0, 6);
            title.setSlug(retrySlug);
            return titleRepository.save(title);
        }
    }
}
