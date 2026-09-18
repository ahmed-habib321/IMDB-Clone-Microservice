package org.example.contributionservice.repository;

import org.example.contributionservice.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, UUID> {
    List<Quote> findByTitleIdAndIsApprovedTrue(UUID titleId);
}
