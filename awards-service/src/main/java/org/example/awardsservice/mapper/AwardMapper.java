package org.example.awardsservice.mapper;


import org.example.awardsservice.dto.AwardResponse;
import org.example.awardsservice.dto.CreateNominationRequest;
import org.example.awardsservice.dto.NominationResponse;
import org.example.awardsservice.dto.TopWinnerResponse;
import org.example.awardsservice.model.Award;
import org.example.awardsservice.model.AwardNomination;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AwardMapper {

    default AwardNomination toEntity(Award award, CreateNominationRequest req) {
        return AwardNomination.builder()
                .award(award)
                .titleId(req.titleId())
                .titleName(req.titleName())
                .personId(req.personId())
                .personName(req.personName())
                .category(req.category())
                .year(req.year())
                .outcome(req.outcome())
                .notes(req.notes())
                .build();
    }

    default TopWinnerResponse toTopWinner(AwardNomination n) {
        boolean isTitle = n.getTitleId() != null;
        UUID    entityId   = isTitle ? n.getTitleId()   : n.getPersonId();
        String  entityName = isTitle ? n.getTitleName() : n.getPersonName();
        String  type       = isTitle ? "TITLE" : "PERSON";
        return new TopWinnerResponse(entityId, entityName, type, 1, 1);
    }

    @Mapping(target = "awardName",         source = "award.name")
    @Mapping(target = "awardAbbreviation", source = "award.abbreviation")
    NominationResponse toNominationResponse(AwardNomination nomination);

    @Mapping(target = "nominationId", source = "id")
    @Mapping(target = "awardName",    source = "award.name")
    AwardResponse toAwardResponse(AwardNomination nomination);

}
