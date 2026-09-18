package org.example.listsservice.repository;

import org.example.listsservice.model.UserList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserListRepository extends JpaRepository<UserList, UUID> {
    Page<UserList> findByUserId(UUID userId, Pageable pageable);
}
