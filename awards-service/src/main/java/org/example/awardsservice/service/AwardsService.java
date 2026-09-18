package org.example.awardsservice.service;

import lombok.RequiredArgsConstructor;
import org.example.awardsservice.dto.*;
import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.example.awardsservice.mapper.AwardMapper;
import org.example.awardsservice.model.Award;
import org.example.awardsservice.model.enums.AwardOutcome;
import org.example.awardsservice.repository.AwardNominationRepository;
import org.example.awardsservice.repository.AwardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwardsService {

    private final AwardRepository            awardRepository;
    private final AwardNominationRepository  nominationRepository;
    private final AwardMapper                awardMapper;

    // ────────────────────────────────────────────────
    //  READ — AWARDS (paginated, filtered)
    // ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<AwardResponse> getTitles(AwardFilterRequest filter, Pageable pageable) {
        return nominationRepository.findWithFilters(
                        filter.year(),
                        filter.awardName(),
                        filter.category(),
                        filter.outcome(),
                        pageable)
                .map(awardMapper::toAwardResponse);
    }

    // ────────────────────────────────────────────────
    //  READ — BY TITLE / PERSON
    // ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<NominationResponse> getTitleNominations(UUID titleId) {
        return nominationRepository.findByTitleId(titleId)
                .stream()
                .map(awardMapper::toNominationResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NominationResponse> getPersonNominations(UUID personId) {
        return nominationRepository.findByPersonId(personId)
                .stream()
                .map(awardMapper::toNominationResponse)
                .toList();
    }

    // ────────────────────────────────────────────────
    //  CREATE
    // ────────────────────────────────────────────────

    @Transactional
    public NominationResponse createNomination(CreateNominationRequest req) {
        Award award = awardRepository.findById(req.awardId())
                .orElseThrow(() -> new ResourceNotFoundException("Award", req.awardId()));

        return awardMapper.toNominationResponse(
                nominationRepository.save(awardMapper.toEntity(award, req)));
    }

    // ────────────────────────────────────────────────
    //  TOP WINNERS
    // ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<TopWinnerResponse> getTopWinners(Integer year, String awardName) {
        return nominationRepository
                .findTopWinners(year, awardName, AwardOutcome.WON, Pageable.ofSize(10))
                .stream()
                .map(awardMapper::toTopWinner)
                .toList();
    }
}
