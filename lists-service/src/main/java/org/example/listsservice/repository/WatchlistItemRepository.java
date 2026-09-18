package org.example.listsservice.repository;

import org.example.listsservice.model.WatchlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, UUID> {

    boolean existsByWatchlistIdAndTitleId(UUID watchlistId, UUID titleId);

    void deleteByWatchlistIdAndTitleId(UUID watchlistId, UUID titleId);

    Optional<WatchlistItem> findByWatchlistIdAndTitleId(UUID watchlistId, UUID titleId);

    Page<WatchlistItem> findByWatchlistId(UUID watchlistId, Pageable pageable);
}