package org.example.peopleservice.mapper;


import org.example.peopleservice.dto.PersonDetailResponse;
import org.example.peopleservice.dto.PersonResponse;
import org.example.peopleservice.dto.UpdatePersonRequest;
import org.example.peopleservice.dto.AddCastRequest;
import org.example.peopleservice.dto.AddCrewRequest;
import org.example.peopleservice.dto.CastResponse;
import org.example.peopleservice.dto.CrewResponse;
import org.example.peopleservice.dto.CreatePersonRequest;
import org.example.sharedmodule.people_service.event.PersonIndexEvent;
import org.example.peopleservice.model.Cast;
import org.example.peopleservice.model.Crew;
import org.example.peopleservice.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PersonMapper {

    default Cast toCast(UUID titleId, Person person, AddCastRequest req) {
        return Cast.builder()
                .titleId(titleId)
                .person(person)
                .characterName(req.characterName())
                .billingOrder(req.billingOrder())
                .isVoice(req.isVoice())
                .episodeCount(req.episodeCount())
                .build();
    }

    default Crew toCrew(UUID titleId, Person person, AddCrewRequest req) {
        return Crew.builder()
                .titleId(titleId)
                .person(person)
                .department(req.department())
                .job(req.job())
                .build();
    }

    PersonResponse toResponse(Person person);

    @Mapping(target = "recentWork", ignore = true)
    PersonDetailResponse toDetailResponse(Person person);

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "createdAt",   ignore = true)
    @Mapping(target = "updatedAt",   ignore = true)
    @Mapping(target = "popularity",  expression = "java(0.0)")
    @Mapping(target = "alsoKnownAs", expression = "java(req.alsoKnownAs() != null ? req.alsoKnownAs() : java.util.List.of())")
    @Mapping(target = "castedIn",    ignore = true)
    @Mapping(target = "workedIn",    ignore = true)
    Person createPerson(CreatePersonRequest req, String slug);

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "slug",        ignore = true)
    @Mapping(target = "createdAt",   ignore = true)
    @Mapping(target = "updatedAt",   ignore = true)
    @Mapping(target = "popularity",  ignore = true)
    @Mapping(target = "castedIn",    ignore = true)
    @Mapping(target = "workedIn",    ignore = true)
    void updatePersonFromRequest(UpdatePersonRequest req, @MappingTarget Person person);

    @Mapping(target = "personId",   source = "person.id")
    @Mapping(target = "personName", source = "person.name")
    @Mapping(target = "personSlug", source = "person.slug")
    @Mapping(target = "profileUrl", source = "person.profileUrl")
    CastResponse toCastResponse(Cast cast);

    @Mapping(target = "personId",   source = "person.id")
    @Mapping(target = "personName", source = "person.name")
    @Mapping(target = "personSlug", source = "person.slug")
    @Mapping(target = "profileUrl", source = "person.profileUrl")
    CrewResponse toCrewResponse(Crew crew);

    @Mapping(target = "personId", source = "id")
    PersonIndexEvent toIndexEvent(Person person);

}
