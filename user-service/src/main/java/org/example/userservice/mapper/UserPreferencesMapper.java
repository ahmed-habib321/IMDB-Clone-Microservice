package org.example.userservice.mapper;

import org.example.userservice.dto.Request.UpdateUserPreferencesRequest;
import org.example.userservice.dto.Response.UserPreferencesResponse;
import org.example.userservice.model.User;
import org.example.userservice.model.UserPreferences;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserPreferencesMapper {

    default UserPreferences createFor(User user) {
        UserPreferences preferences = new UserPreferences();
        preferences.setUser(user);
        return preferences;
    }

    UserPreferencesResponse toResponse(UserPreferences user);

    void updatePreferences(UpdateUserPreferencesRequest request, @MappingTarget UserPreferences Preferences);

}
