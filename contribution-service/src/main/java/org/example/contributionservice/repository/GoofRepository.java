package org.example.contributionservice.repository;

import org.example.contributionservice.model.Goof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoofRepository extends JpaRepository<Goof, UUID> {
    List<Goof> findByTitleIdAndIsApprovedTrue(UUID titleId);

    List<Goof> findByTitleIdAndIsApprovedFalse(UUID titleId);
}
