package org.example.contributionservice.service;

import lombok.RequiredArgsConstructor;
import org.example.contributionservice.dto.GoofRequest;
import org.example.contributionservice.dto.GoofResponse;
import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.example.contributionservice.mapper.ContributionMapper;
import org.example.contributionservice.model.Goof;
import org.example.contributionservice.repository.GoofRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoofService {

    private final GoofRepository goofRepository;
    private final ContributionMapper contributionMapper;

    @Transactional(readOnly = true)
    public List<GoofResponse> getApproved(UUID titleId) {
        return goofRepository.findByTitleIdAndIsApprovedTrue(titleId)
                .stream()
                .map(contributionMapper::toGoofResponse)
                .toList();
    }

    @Transactional
    public GoofResponse create(UUID titleId, UUID userId, GoofRequest req) {
        Goof saved = goofRepository.save(contributionMapper.toEntity(titleId, userId, req));
        return contributionMapper.toGoofResponse(saved);
    }

    @Transactional
    public void approve(UUID goofId) {
        Goof goof = goofRepository.findById(goofId)
                .orElseThrow(() -> new ResourceNotFoundException("Goof", goofId));
        goof.setIsApproved(true);
        goofRepository.save(goof);
    }

    @Transactional
    public void delete(UUID goofId) {
        if (!goofRepository.existsById(goofId))
            throw new ResourceNotFoundException("Goof", goofId);
        goofRepository.deleteById(goofId);
    }
}
