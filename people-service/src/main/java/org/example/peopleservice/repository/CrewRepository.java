package org.example.peopleservice.repository;

import org.example.peopleservice.model.Crew;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CrewRepository extends JpaRepository<Crew, UUID> {

    @EntityGraph(attributePaths = {"person"})
    List<Crew> findByTitleId(UUID titleId);

    @EntityGraph(attributePaths = {"person"})
    Page<Crew> findByTitleId(UUID titleId, Pageable pageable);
}