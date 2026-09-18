package org.example.listsservice.repository;

import org.example.listsservice.model.UserListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserListItemRepository extends JpaRepository<UserListItem, UUID> {
    Optional<UserListItem> findByListIdAndTitleId(UUID listId, UUID titleId);
    boolean existsByListIdAndTitleId(UUID listId, UUID titleId);
    void deleteByListIdAndTitleId(UUID listId, UUID titleId);
}
