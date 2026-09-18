package org.example.listsservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "watchlist_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"watchlist_id", "title_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class WatchlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watchlist_id", nullable = false)
    private Watchlist watchlist;

    @Column(name = "title_id", nullable = false)
    private UUID titleId;
    private Instant addedAt;
    private Boolean watched;
    private Instant watchedAt;
    private String notes;
}