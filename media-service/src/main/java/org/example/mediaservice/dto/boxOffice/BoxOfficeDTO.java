package org.example.mediaservice.dto.boxOffice;

import java.util.UUID;

public record BoxOfficeDTO(
        UUID titleId,
        UUID id,
        Long budget,
        Long openingWeekend,
        Long domestic,
        Long international,
        Long worldwide,
        String currency,
        String source
) {}
