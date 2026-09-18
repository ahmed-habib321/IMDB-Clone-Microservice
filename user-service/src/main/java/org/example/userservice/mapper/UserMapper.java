package org.example.userservice.mapper;

import org.example.sharedmodule.user_service.dto.UserDTO;
import org.example.userservice.dto.Request.UpdateUserRequest;
import org.example.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    default User createUser(UUID userId, String email, String username) {
        return User.builder()
                .id(userId)
                .email(email)
                .username(username)
                .build();
    }

    void updateUser(UpdateUserRequest request, @MappingTarget User user);

    UserDTO toDTO(User user);
}