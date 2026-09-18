package org.example.awardsservice.repository;


import org.example.awardsservice.model.AwardNomination;
import org.example.awardsservice.model.enums.AwardOutcome;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AwardNominationRepository extends JpaRepository<AwardNomination, UUID> {

    List<AwardNomination> findByTitleId(UUID titleId);

    List<AwardNomination> findByPersonId(UUID personId);

    @Query("""
            SELECT n FROM AwardNomination n WHERE
            (:year     IS NULL OR n.year        = :year)     AND
            (:awardName IS NULL OR n.award.name = :awardName) AND
            n.outcome = :outcome
            ORDER BY n.year DESC
            """)
    Page<AwardNomination> findTopWinners(
            @Param("year")      Integer year,
            @Param("awardName") String  awardName,
            @Param("outcome")   AwardOutcome outcome,
            Pageable pageable);

    @Query("""
            SELECT n FROM AwardNomination n WHERE
            (:year       IS NULL OR n.year        = :year)      AND
            (:awardName  IS NULL OR n.award.name  = :awardName) AND
            (:category   IS NULL OR n.category    = :category)  AND
            (:outcomeStr IS NULL OR CAST(n.outcome AS string) = :outcomeStr)
            """)
    Page<AwardNomination> findWithFilters(
            @Param("year")       Integer year,
            @Param("awardName")  String  awardName,
            @Param("category")   String  category,
            @Param("outcomeStr") String  outcomeStr,
            Pageable pageable);
}
