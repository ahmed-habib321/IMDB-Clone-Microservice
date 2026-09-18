package org.example.listsservice.service;

import lombok.RequiredArgsConstructor;
import org.example.listsservice.dto.WatchlistItemResponse;
import org.example.sharedmodule.general_exceptions.BusinessException;
import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.example.listsservice.mapper.WatchlistMapper;
import org.example.listsservice.model.Watchlist;
import org.example.listsservice.model.WatchlistItem;
import org.example.listsservice.repository.WatchlistItemRepository;
import org.example.listsservice.repository.WatchlistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository     watchlistRepository;
    private final WatchlistItemRepository itemRepository;
    private final WatchlistMapper         watchlistMapper;


    // ────────────────────────────────────────────────
    //  READ
    // ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<WatchlistItemResponse> getWatchlist(UUID userId, Pageable pageable) {
        Watchlist watchlist = getOrCreateWatchlist(userId);
        return itemRepository.findByWatchlistId(watchlist.getId(), pageable)
                .map(watchlistMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public boolean isInWatchlist(UUID userId, UUID titleId) {
        Watchlist watchlist = getOrCreateWatchlist(userId);
        return itemRepository.existsByWatchlistIdAndTitleId(watchlist.getId(), titleId);
    }

    // ────────────────────────────────────────────────
    //  ADD / REMOVE
    // ────────────────────────────────────────────────

    @Transactional
    public WatchlistItemResponse addToWatchlist(UUID userId, UUID titleId) {
        Watchlist watchlist = getOrCreateWatchlist(userId);

        if (itemRepository.existsByWatchlistIdAndTitleId(watchlist.getId(), titleId))
            throw new BusinessException("Title is already in your watchlist", HttpStatus.UNPROCESSABLE_CONTENT);

        WatchlistItem item = watchlistMapper.toItem(watchlist, titleId);

        WatchlistItem saved = itemRepository.save(item);

        watchlist.setItemCount(watchlist.getItemCount() == null ? 1 : watchlist.getItemCount() + 1);
        watchlistRepository.save(watchlist);

        return watchlistMapper.toResponse(saved);
    }

    @Transactional
    public void removeFromWatchlist(UUID userId, UUID titleId) {
        Watchlist watchlist = getOrCreateWatchlist(userId);

        if (!itemRepository.existsByWatchlistIdAndTitleId(watchlist.getId(), titleId))
            throw new ResourceNotFoundException("Title not in watchlist");

        itemRepository.deleteByWatchlistIdAndTitleId(watchlist.getId(), titleId);

        if (watchlist.getItemCount() != null && watchlist.getItemCount() > 0) {
            watchlist.setItemCount(watchlist.getItemCount() - 1);
            watchlistRepository.save(watchlist);
        }
    }

    // ────────────────────────────────────────────────
    //  MARK WATCHED
    // ────────────────────────────────────────────────

    @Transactional
    public WatchlistItemResponse markWatched(UUID userId, UUID titleId, boolean watched) {
        Watchlist watchlist = getOrCreateWatchlist(userId);

        WatchlistItem item = itemRepository
                .findByWatchlistIdAndTitleId(watchlist.getId(), titleId)
                .orElseThrow(() -> new ResourceNotFoundException("Title not in watchlist"));

        item.setWatched(watched);
        item.setWatchedAt(watched ? Instant.now() : null);

        return watchlistMapper.toResponse(itemRepository.save(item));
    }

    // ────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ────────────────────────────────────────────────

    private Watchlist getOrCreateWatchlist(UUID userId) {
        return watchlistRepository.findByUserId(userId)
                .orElseGet(() -> watchlistRepository.save(watchlistMapper.create(userId)));
    }

}
