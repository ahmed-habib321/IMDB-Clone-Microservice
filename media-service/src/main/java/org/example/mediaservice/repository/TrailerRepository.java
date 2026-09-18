package org.example.mediaservice.repository;


import org.example.mediaservice.model.Trailer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrailerRepository extends JpaRepository<Trailer, UUID> {
    List<Trailer> findByTitleId(UUID titleId);
}