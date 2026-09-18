package org.example.mediaservice.service;


import lombok.RequiredArgsConstructor;
import org.example.mediaservice.dto.boxOffice.BoxOfficeDTO;
import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.example.mediaservice.mapper.MediaMapper;
import org.example.mediaservice.model.BoxOffice;
import org.example.mediaservice.repository.BoxOfficeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BoxOfficeService {

    private final BoxOfficeRepository boxOfficeRepository;
    private final MediaMapper mediaMapper;

    @Transactional(readOnly = true)
    public BoxOfficeDTO getBoxOffice(UUID titleId) {
        return boxOfficeRepository.findByTitleId(titleId)
                .map(mediaMapper::toBoxOfficeResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Box office data not found for title: " + titleId));
    }

    @Transactional
    public BoxOfficeDTO upsertBoxOffice(UUID titleId, BoxOfficeDTO req) {

        //TODO check if the title exist
        BoxOffice bo = boxOfficeRepository.findByTitleId(titleId)
                .orElse(mediaMapper.create(titleId));

        mediaMapper.updateFromRequest(req, bo);

        // TODO update title.revenue
        return mediaMapper.toBoxOfficeResponse(boxOfficeRepository.save(bo));
    }

    @Transactional(readOnly = true)
    public List<BoxOfficeDTO> getTopGrossing(int limit) {
        limit = Math.max(1, Math.min(limit, 100));
        return boxOfficeRepository.findTopGrossing(limit)
                .stream()
                .map(mediaMapper::toBoxOfficeResponse)
                .toList();
    }

}
