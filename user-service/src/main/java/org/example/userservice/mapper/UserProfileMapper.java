package org.example.userservice.mapper;

import org.example.userservice.dto.Response.UserProfileResponse;
import org.example.userservice.dto.Request.UpdateUserProfileRequest;
import org.example.userservice.model.User;
import org.example.userservice.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserProfileMapper {

    default UserProfile createFor(User user) {
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        return profile;
    }

    UserProfileResponse toResponse(UserProfile user);

    void updateProfile(UpdateUserProfileRequest request, @MappingTarget UserProfile profile);

}
