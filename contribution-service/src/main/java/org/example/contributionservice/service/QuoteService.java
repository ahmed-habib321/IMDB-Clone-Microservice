package org.example.contributionservice.service;

import lombok.RequiredArgsConstructor;
import org.example.contributionservice.dto.QuoteRequest;
import org.example.contributionservice.dto.QuoteResponse;
import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.example.contributionservice.mapper.ContributionMapper;
import org.example.contributionservice.model.Quote;
import org.example.contributionservice.repository.QuoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository    quoteRepository;
    private final ContributionMapper contributionMapper;

    @Transactional(readOnly = true)
    public List<QuoteResponse> getApproved(UUID titleId) {
        return quoteRepository.findByTitleIdAndIsApprovedTrue(titleId)
                .stream()
                .map(contributionMapper::toQuoteResponse)
                .toList();
    }

    @Transactional
    public QuoteResponse create(UUID titleId, UUID userId, QuoteRequest req) {
        Quote saved = quoteRepository.save(contributionMapper.toEntity(titleId, userId, req));
        return contributionMapper.toQuoteResponse(saved);
    }

    @Transactional
    public void approve(UUID quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote", quoteId));
        quote.setIsApproved(true);
        quoteRepository.save(quote);
    }

    @Transactional
    public void delete(UUID quoteId) {
        if (!quoteRepository.existsById(quoteId))
            throw new ResourceNotFoundException("Quote", quoteId);
        quoteRepository.deleteById(quoteId);
    }
}
