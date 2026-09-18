package org.example.titleservice.mapper;

import org.example.sharedmodule.title_service.dto.TitleDocument;
import org.example.sharedmodule.title_service.dto.TitleMiniResponse;
import org.example.sharedmodule.title_service.event.TitleIndexEvent;
import org.example.titleservice.dto.CreateTitle.CreateMovieRequest;
import org.example.titleservice.dto.CreateTitle.CreateTvShowRequest;
import org.example.titleservice.dto.CreateTitle.CreateEpisodeRequest;
import org.example.titleservice.dto.CreateTitle.CreateSeasonRequest;
import org.example.titleservice.dto.Responses.EpisodeFullResponse;
import org.example.titleservice.dto.Responses.MovieFullResponse;
import org.example.titleservice.dto.Responses.SeasonFullResponse;
import org.example.titleservice.dto.Responses.ShowFullResponse;
import org.example.titleservice.dto.Responses.TitleCardResponse;
import org.example.titleservice.dto.UpdateTitle.UpdateMovieRequest;
import org.example.titleservice.dto.UpdateTitle.UpdateTvShowRequest;
import org.example.titleservice.model.*;
import org.mapstruct.*;

import java.util.List;


@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TitleMapper {

    // ── Create mappings ───────────────────────────────────────────────
    @Mapping(target = "id",             ignore = true)
    @Mapping(target = "slug",           ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "updatedAt",      ignore = true)
    @Mapping(target = "imdbRating",     ignore = true)
    @Mapping(target = "voteCount",      ignore = true)
    @Mapping(target = "popularity",     ignore = true)
    @Mapping(target = "titleType",      expression = "java(org.example.sharedmodule.title_service.enums.TitleType.MOVIE)")
    Movie createMovie(CreateMovieRequest request);

    @Mapping(target = "id",             ignore = true)
    @Mapping(target = "slug",           ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "updatedAt",      ignore = true)
    @Mapping(target = "imdbRating",     ignore = true)
    @Mapping(target = "voteCount",      ignore = true)
    @Mapping(target = "popularity",     ignore = true)
    @Mapping(target = "seasons",        ignore = true)
    @Mapping(target = "titleType",      expression = "java(org.example.sharedmodule.title_service.enums.TitleType.TV_SERIES)")
    TvShow createShow(CreateTvShowRequest request);



    // ── Lightweight list / search responses ──────────────────────────

    TitleMiniResponse toTitleMiniResponse(Title title);

    @Mapping(target = "genres", source = "genres")
    TitleCardResponse toTitleCardResponse(Title title);

    // ── Index event (full searchable document carried on the wire) ────

    @Mapping(target = "titleId", source = "id")
    TitleIndexEvent toIndexEvent(Title title);


    // ── Full detail responses ─────────────────────────────────────────

    @Mapping(target = "genres",    source = "genres")
    @Mapping(target = "languages", source = "languages")
    @Mapping(target = "countries", source = "countries")
    MovieFullResponse toMovieFullResponse(Movie movie);

    @Mapping(target = "genres",    source = "genres")
    @Mapping(target = "languages", source = "languages")
    @Mapping(target = "countries", source = "countries")
    ShowFullResponse toShowFullResponse(TvShow show);

    // ── Season / Episode ──────────────────────────────────────────────

    SeasonFullResponse toSeasonFullResponse(Season season);
    EpisodeFullResponse toEpisodeFullResponse(Episode episode);

    // ── Update mappings (in-place, nulls ignored) ─────────────────────

    @Mapping(target = "id",             ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "updatedAt",      ignore = true)
    void updateMovieFromRequest(UpdateMovieRequest req, @MappingTarget Movie movie);


    @Mapping(target = "id",             ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "updatedAt",      ignore = true)
    void updateShowFromRequest(UpdateTvShowRequest req, @MappingTarget TvShow show);

    @Mapping(target = "id",       ignore = true)
    @Mapping(target = "tvShow",   ignore = true)   // set by @AfterMapping
    @Mapping(target = "episodes", source = "episodes")
    Season toSeason(CreateSeasonRequest dto, @Context TvShow show);

    List<Season> toSeasons(List<CreateSeasonRequest> dtos, @Context TvShow show);


    // Episode single-element mapping
    @Mapping(target = "id",     ignore = true)
    @Mapping(target = "season", ignore = true)  // set by @AfterMapping
    Episode toEpisode(CreateEpisodeRequest dto);


    List<Episode> toEpisodes(List<CreateEpisodeRequest> dtos);

    @AfterMapping
    default void afterSeason(@MappingTarget Season season, @Context TvShow show) {
        season.setTvShow(show);
        if (season.getEpisodes() != null) season.getEpisodes().forEach(ep -> ep.setSeason(season));
    }



    default TitleDocument toTitleDocument(Title title){
        return TitleDocument.builder()
                .id(String.valueOf(title.getId()))
                .primaryTitle(title.getPrimaryTitle())
                .originalTitle(title.getOriginalTitle())
                .titleType(String.valueOf(title.getTitleType()))
                .status(String.valueOf(title.getStatus()))
                .releaseDate(title.getReleaseDate())
                .adult(title.getAdult())
                .imdbRating(title.getImdbRating())
                .genres(title.getGenres())
                .languages(title.getLanguages())
                .countries(title.getCountries())
                .build();
    }

}
