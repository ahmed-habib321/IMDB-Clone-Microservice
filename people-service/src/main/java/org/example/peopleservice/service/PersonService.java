package org.example.peopleservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.outbox.service.OutboxWriter;
import org.example.peopleservice.client.TitleServiceClient;
import org.example.peopleservice.dto.*;
import org.example.peopleservice.mapper.PersonMapper;
import org.example.peopleservice.model.Cast;
import org.example.peopleservice.model.Crew;
import org.example.peopleservice.model.Person;
import org.example.peopleservice.repository.CastRepository;
import org.example.peopleservice.repository.CrewRepository;
import org.example.peopleservice.repository.PersonRepository;
import org.example.sharedmodule.people_service.event.PersonCreatedEvent;
import org.example.sharedmodule.people_service.event.PersonIndexEvent;
import org.example.sharedmodule.people_service.event.PersonUpdatedEvent;
import org.example.sharedmodule.people_service.exception.PersonNotFoundException;
import org.example.sharedmodule.title_service.dto.TitleMiniResponse;
import org.example.sharedmodule.utils.LogUtils;
import org.example.sharedmodule.utils.SlugUtils;
import org.example.sharedmodule.utils.UUIDUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.example.sharedmodule.Constants.SERVICE_NAMES.PEOPLE_SERVICE;
import static org.example.sharedmodule.Constants.TOPIC_NAMES.PERSON_CREATED;
import static org.example.sharedmodule.Constants.TOPIC_NAMES.PERSON_INDEXED;
import static org.example.sharedmodule.Constants.TOPIC_NAMES.PERSON_UPDATED;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final CastRepository castRepository;
    private final CrewRepository crewRepository;
    private final PersonMapper personMapper;
    private final TitleServiceClient titleServiceClient;
    private final OutboxWriter outboxWriter;


    @Transactional
    public PersonResponse createPerson(CreatePersonRequest req) {
        for (int attempt = 0; attempt < 3; attempt++) {
            String slug = SlugUtils.generateUniqueSlug(req.name(), personRepository::existsBySlug);

            Person person = personMapper.createPerson(req, slug);

            try {
                Person saved = personRepository.saveAndFlush(person);
                publishCreated(saved);
                return personMapper.toResponse(saved);
            } catch (DataIntegrityViolationException e) {
                log.warn("Slug collision on '{}', retrying (attempt {}).", slug, attempt + 1);
            }
        }
        throw new DataIntegrityViolationException("Unable to create person with a unique slug for name: " + req.name());
    }

    @Transactional(readOnly = true)
    public PersonResponse getPerson(String id) {
        Person person = findExistingPerson(UUIDUtils.parse(id));
        return personMapper.toResponse(person);
    }



    @Transactional(readOnly = true)
    public PersonDetailResponse getPersonBySlug(String slug) {
        Person person = personRepository.findBySlugWithDetails(slug)
                .orElseThrow(() -> new PersonNotFoundException(slug));
        return personMapper.toDetailResponse(person);
    }

    @Transactional(readOnly = true)
    public PersonDetailResponse getPersonDetail(String id) {
        Person person = personRepository.findByIdWithDetails(UUIDUtils.parse(id))
                .orElseThrow(() -> new PersonNotFoundException(id));
        return personMapper.toDetailResponse(person);
    }


    @Transactional(readOnly = true)
    public Page<TitleMiniResponse> getFilmography(UUID personId, Pageable pageable) {
        Person person = findExistingPerson(personId);

        Set<UUID> titleIds = new HashSet<>();
        person.getCastedIn().stream().map(Cast::getTitleId).forEach(titleIds::add);
        person.getWorkedIn().stream().map(Crew::getTitleId).forEach(titleIds::add);

        if (titleIds.isEmpty()) return Page.empty(pageable);

        List<TitleMiniResponse> filmography = titleServiceClient.fetchTitles(titleIds);

        int start = (int) Math.min(pageable.getOffset(), filmography.size());
        int end = (int) Math.min(pageable.getOffset() + pageable.getPageSize(), filmography.size());
        List<TitleMiniResponse> content = start >= end ? List.of() : filmography.subList(start, end);
        return new PageImpl<>(content, pageable, filmography.size());
    }

    @Transactional
    public PersonResponse updatePerson(String id, UpdatePersonRequest req) {
        Person person = findExistingPerson(UUIDUtils.parse(id));

        String previousName = person.getName();
        personMapper.updatePersonFromRequest(req, person);

        if (req.name() != null && !req.name().isBlank() && !req.name().equalsIgnoreCase(previousName)) {
            person.setSlug(SlugUtils.generateUniqueSlug(person.getName(), personRepository::existsBySlug));
        } else if (person.getSlug() == null || person.getSlug().isBlank()) {
            person.setSlug(SlugUtils.generateUniqueSlug(person.getName(), personRepository::existsBySlug));
        }

        Person saved = personRepository.save(person);
        publishUpdated(saved);

        return personMapper.toResponse(saved);
    }


    // ────────────────────────────────────────────────
    //  CAST
    // ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CastResponse> getCast(String titleId) {
        UUID uuid = UUIDUtils.parse(titleId);

        return castRepository.findByTitleIdOrderByBillingOrderAsc(uuid)
                .stream()
                .map(personMapper::toCastResponse)
                .toList();
    }

    @Transactional
    public CastResponse addCastMember(String titleId, AddCastRequest req) {
        UUID uuid = UUIDUtils.parse(titleId);

        Person person = findExistingPerson(req.personId());

        Cast saved = castRepository.save(personMapper.toCast(uuid, person, req));

        return personMapper.toCastResponse(saved);
    }

    // ────────────────────────────────────────────────
    //  CREW
    // ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CrewResponse> getCrew(String titleId) {
        UUID uuid = UUIDUtils.parse(titleId);

        return crewRepository.findByTitleId(uuid)
                .stream()
                .map(personMapper::toCrewResponse)
                .toList();
    }

    @Transactional
    public CrewResponse addCrewMember(String titleId, AddCrewRequest req) {
        UUID uuid = UUIDUtils.parse(titleId);

        Person person = findExistingPerson(req.personId());

        Crew saved = crewRepository.save(personMapper.toCrew(uuid, person, req));

        return personMapper.toCrewResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<PersonResponse> searchPeople(PersonSearchQuery filter, Pageable pageable) {
        Page<Person> people = personRepository.findWithFilters(
                filter.name(),
                filter.gender(),
                filter.minPopularity(),
                filter.department(),
                pageable);
        return people.map(personMapper::toResponse);
    }

    private Person findExistingPerson(UUID uuid) {
        return personRepository.findById(uuid)
                .orElseThrow(() -> new PersonNotFoundException(uuid));
    }

    private void publishCreated(Person person) {
        outboxWriter.save(new PersonCreatedEvent(
                        person.getId(), person.getName(), person.getSlug(),
                        person.getAlsoKnownAs(), person.getBiography(), person.getProfileUrl(),
                        person.getBirthDate(), person.getGender(), person.getPopularity(),
                        person.getImdbId(), Instant.now()),
                PERSON_CREATED, person.getId().toString());
        outboxWriter.save(personMapper.toIndexEvent(person),
                PERSON_INDEXED, person.getId().toString());
        LogUtils.logEventPublished(PEOPLE_SERVICE, PERSON_CREATED);
    }

    private void publishUpdated(Person person) {
        outboxWriter.save(new PersonUpdatedEvent(
                        person.getId(), person.getName(), person.getSlug(),
                        person.getAlsoKnownAs(), person.getBiography(), person.getProfileUrl(),
                        person.getBirthDate(), person.getGender(), person.getPopularity(),
                        person.getImdbId(), Instant.now()),
                PERSON_UPDATED, person.getId().toString());
        outboxWriter.save(personMapper.toIndexEvent(person),
                PERSON_INDEXED, person.getId().toString());
        LogUtils.logEventPublished(PEOPLE_SERVICE, PERSON_UPDATED);
    }

}
