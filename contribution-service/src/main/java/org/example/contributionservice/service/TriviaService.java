package org.example.contributionservice.service;

import lombok.RequiredArgsConstructor;
import org.example.contributionservice.dto.TriviaRequest;
import org.example.contributionservice.dto.TriviaResponse;
import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.example.contributionservice.mapper.ContributionMapper;
import org.example.contributionservice.model.Trivia;
import org.example.contributionservice.repository.TriviaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TriviaService {

    private final TriviaRepository   triviaRepository;
    private final ContributionMapper contributionMapper;

    // ────────────────────────────────────────────────
    //  READ
    // ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<TriviaResponse> getApproved(UUID titleId, Pageable pageable) {
        return triviaRepository.findByTitleIdAndIsApprovedTrue(titleId, pageable)
                .map(contributionMapper::toTriviaResponse);
    }

    // ────────────────────────────────────────────────
    //  CREATE
    // ────────────────────────────────────────────────

    @Transactional
    public TriviaResponse create(UUID titleId, UUID userId, TriviaRequest req) {
        Trivia saved = triviaRepository.save(contributionMapper.toEntity(titleId, userId, req));
        return contributionMapper.toTriviaResponse(saved);
    }

    // ────────────────────────────────────────────────
    //  MODERATION
    // ────────────────────────────────────────────────

    @Transactional
    public void approve(UUID triviaId) {
        Trivia trivia = triviaRepository.findById(triviaId)
                .orElseThrow(() -> new ResourceNotFoundException("Trivia", triviaId));
        trivia.setIsApproved(true);
        triviaRepository.save(trivia);
    }

    @Transactional
    public void delete(UUID triviaId) {
        if (!triviaRepository.existsById(triviaId))
            throw new ResourceNotFoundException("Trivia", triviaId);
        triviaRepository.deleteById(triviaId);
    }
}
