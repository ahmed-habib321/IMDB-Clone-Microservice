package org.example.listsservice.mapper;

import org.example.listsservice.dto.CreateListRequest;
import org.example.listsservice.dto.ListResponse;
import org.example.listsservice.dto.UpdateListRequest;
import org.example.listsservice.dto.UserListDetailResponse;
import org.example.listsservice.dto.UserListResponse;
import org.example.listsservice.model.UserList;
import org.example.listsservice.model.UserListItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserListMapper {

    UserListResponse toResponse(UserList list);

    ListResponse toListResponse(UserList list);

    default UserListDetailResponse toDetailResponse(UserList list) {
        List<UUID> titleIds = list.getItems() == null ? List.of() :
                list.getItems().stream()
                        .map(UserListItem::getTitleId)
                        .toList();
        return new UserListDetailResponse(
                list.getId(), list.getName(), list.getDescription(),
                list.getIsPublic(), list.getItemCount(), list.getCreatedAt(),
                titleIds);
    }

    default UserListItem toItem(UserList list, UUID titleId) {
        return UserListItem.builder()
                .list(list)
                .titleId(titleId)
                .addedAt(Instant.now())
                .build();
    }

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "userId",    ignore = true)
    @Mapping(target = "itemCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "items",     ignore = true)
    UserList toEntity(CreateListRequest req);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "userId",    ignore = true)
    @Mapping(target = "itemCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "items",     ignore = true)
    void updateFromRequest(UpdateListRequest req, @MappingTarget UserList list);
}