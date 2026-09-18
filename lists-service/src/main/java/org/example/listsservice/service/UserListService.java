package org.example.listsservice.service;

import lombok.RequiredArgsConstructor;
import org.example.listsservice.dto.*;
import org.example.sharedmodule.general_exceptions.BusinessException;
import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.example.listsservice.mapper.UserListMapper;
import org.example.listsservice.model.UserList;
import org.example.listsservice.repository.UserListItemRepository;
import org.example.listsservice.repository.UserListRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserListService {

    private final UserListRepository listRepository;
    private final UserListItemRepository itemRepository;
    private final UserListMapper listMapper;

    // ────────────────────────────────────────────────
    //  READ
    // ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<UserListResponse> getLists(UUID userId, Pageable pageable) {
        return listRepository.findByUserId(userId, pageable)
                .map(listMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ListResponse> getUserLists(UUID userId, Pageable pageable) {
        return listRepository.findByUserId(userId, pageable)
                .map(listMapper::toListResponse);
    }

    @Transactional(readOnly = true)
    public UserListDetailResponse getList(UUID id) {
        UserList list = findListById(id);
        return listMapper.toDetailResponse(list);
    }

    // ────────────────────────────────────────────────
    //  CREATE / UPDATE / DELETE
    // ────────────────────────────────────────────────

    @Transactional
    public UserListResponse createList(UUID userId, CreateListRequest req) {
        // MapStruct fills name, description, isPublic
        UserList list = listMapper.toEntity(req);
        list.setUserId(userId);
        list.setItemCount(0);
        return listMapper.toResponse(listRepository.save(list));
    }

    @Transactional
    public UserListResponse updateList(UUID id, UUID userId, UpdateListRequest req) {
        UserList list = findListById(id);
        assertOwner(list.getUserId(), userId, "modify");
        listMapper.updateFromRequest(req, list);
        return listMapper.toResponse(listRepository.save(list));
    }

    @Transactional
    public void deleteList(UUID id, UUID userId) {
        UserList list = findListById(id);
        assertOwner(list.getUserId(), userId, "delete");
        listRepository.delete(list);
    }

    // ────────────────────────────────────────────────
    //  ITEMS
    // ────────────────────────────────────────────────

    @Transactional
    public void addItem(UUID listId, UUID titleId, UUID userId) {
        UserList list = findListById(listId);
        assertOwner(list.getUserId(), userId, "modify");

        if (itemRepository.existsByListIdAndTitleId(listId, titleId))
            throw new BusinessException("Title is already in this list", HttpStatus.UNPROCESSABLE_CONTENT);

        itemRepository.save(listMapper.toItem(list, titleId));

        list.setItemCount(list.getItemCount() == null ? 1 : list.getItemCount() + 1);
        listRepository.save(list);
    }

    @Transactional
    public void removeItem(UUID listId, UUID titleId, UUID userId) {
        UserList list = findListById(listId);
        assertOwner(list.getUserId(), userId, "modify");

        if (!itemRepository.existsByListIdAndTitleId(listId, titleId))
            throw new ResourceNotFoundException("Title not found in this list");

        itemRepository.deleteByListIdAndTitleId(listId, titleId);

        if (list.getItemCount() != null && list.getItemCount() > 0)
            list.setItemCount(list.getItemCount() - 1);
        listRepository.save(list);
    }

    // ────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ────────────────────────────────────────────────

    private UserList findListById(UUID id) {
        return listRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("List", id));
    }

    private void assertOwner(UUID ownerId, UUID requesterId, String action) {
        if (!ownerId.equals(requesterId))
            throw new BusinessException("Not authorized to " + action + " this list", HttpStatus.UNPROCESSABLE_CONTENT);
    }

}
