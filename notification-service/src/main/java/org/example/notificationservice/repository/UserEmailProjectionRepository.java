package org.example.notificationservice.repository;

import org.example.notificationservice.entity.UserEmailProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserEmailProjectionRepository extends JpaRepository<UserEmailProjection, UUID> {
}
