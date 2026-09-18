package org.example.listsservice.repository;

import org.example.listsservice.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WatchlistRepository extends JpaRepository<Watchlist, UUID> {
    Optional<Watchlist> findByUserId(UUID userId);
}