package org.example.contributionservice.repository;

import org.example.contributionservice.model.Trivia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TriviaRepository extends JpaRepository<Trivia, UUID> {
    Page<Trivia> findByTitleIdAndIsApprovedTrue(UUID titleId, Pageable pageable);

    Page<Trivia> findByIsApprovedFalse(Pageable pageable);

    long countByIsApprovedTrue();

}
