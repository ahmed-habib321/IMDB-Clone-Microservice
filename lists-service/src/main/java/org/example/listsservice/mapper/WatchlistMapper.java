package org.example.listsservice.mapper;

import org.example.listsservice.dto.WatchlistItemResponse;
import org.example.listsservice.model.Watchlist;
import org.example.listsservice.model.WatchlistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.Instant;
import java.util.UUID;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface WatchlistMapper {

    default Watchlist create(UUID userId) {
        return Watchlist.builder()
                .userId(userId)
                .itemCount(0)
                .build();
    }

    default WatchlistItem toItem(Watchlist watchlist, UUID titleId) {
        return WatchlistItem.builder()
                .watchlist(watchlist)
                .titleId(titleId)      // ✅ scalar UUID — no TitleRepository needed
                .watched(false)
                .addedAt(Instant.now())
                .build();
    }

    /**
     * WatchlistItem.titleId is a UUID scalar — title metadata (primaryTitle, slug,
     * posterUrl, imdbRating) is NOT available in this service.
     * Map only what we own; the API gateway or client enriches with title-service data.
     */
    @Mapping(target = "primaryTitle", ignore = true)
    @Mapping(target = "slug",         ignore = true)
    @Mapping(target = "posterUrl",    ignore = true)
    @Mapping(target = "imdbRating",   ignore = true)
    WatchlistItemResponse toResponse(WatchlistItem item);
}
