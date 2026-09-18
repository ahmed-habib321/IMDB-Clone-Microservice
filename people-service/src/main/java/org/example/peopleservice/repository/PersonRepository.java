package org.example.peopleservice.repository;

import org.example.peopleservice.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {

    Optional<Person> findPeopleById(UUID id);

    boolean existsBySlug(String slug);

    @EntityGraph(attributePaths = {"castedIn", "workedIn"})
    @Query("SELECT p FROM Person p WHERE p.slug = :slug")
    Optional<Person> findBySlugWithDetails(@Param("slug") String slug);

    @EntityGraph(attributePaths = {"castedIn", "workedIn"})
    @Query("SELECT p FROM Person p WHERE p.id = :id")
    Optional<Person> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT p FROM Person p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:gender IS NULL OR p.gender = :gender) " +
            "AND (:minPopularity IS NULL OR p.popularity >= :minPopularity) " +
            "AND (:department IS NULL OR EXISTS " +
            "     (SELECT 1 FROM Crew c WHERE c.person = p AND c.department = :department))")
    Page<Person> findWithFilters(
            @Param("name") String name,
            @Param("gender") String gender,
            @Param("minPopularity") Double minPopularity,
            @Param("department") String department,
            Pageable pageable);
}
