package org.example.mediaservice.repository;

import org.example.mediaservice.model.BoxOffice;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoxOfficeRepository extends JpaRepository<BoxOffice, UUID> {

    Optional<BoxOffice> findByTitleId(UUID titleId);

    @Query("SELECT b FROM BoxOffice b WHERE b.worldwide IS NOT NULL ORDER BY b.worldwide DESC")
    List<BoxOffice> findTopGrossing(Pageable pageable);

    default List<BoxOffice> findTopGrossing(int limit) {
        return findTopGrossing(PageRequest.of(0, limit));
    }
}
