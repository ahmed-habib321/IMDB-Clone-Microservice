package org.example.peopleservice.repository;


import org.example.peopleservice.model.Cast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CastRepository extends JpaRepository<Cast, UUID> {
    @EntityGraph(attributePaths = {"person"})
    List<Cast> findByTitleIdOrderByBillingOrderAsc(UUID titleId);

    @EntityGraph(attributePaths = {"person"})
    Page<Cast> findByTitleId(UUID titleId, Pageable pageable);
}
