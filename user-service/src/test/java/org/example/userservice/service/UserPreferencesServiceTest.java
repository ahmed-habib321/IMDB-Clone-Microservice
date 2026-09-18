package org.example.userservice.service;

import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.example.userservice.dto.Request.UpdateUserPreferencesRequest;
import org.example.userservice.dto.Response.UserPreferencesResponse;
import org.example.userservice.mapper.UserPreferencesMapper;
import org.example.userservice.model.User;
import org.example.userservice.model.UserPreferences;
import org.example.userservice.repository.UserPreferencesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPreferencesServiceTest {

    @Mock UserPreferencesRepository userPreferencesRepository;
    @Mock UserPreferencesMapper userPreferencesMapper;

    @InjectMocks UserPreferencesService userPreferencesService;

    private static final UUID USER_ID = UUID.randomUUID();

    private User buildUser() {
        User user = new User();
        user.setId(USER_ID);
        return user;
    }

    @Test
    void ensureDefaultsFor_shouldCreateAndPersist_whenMissing() {
        User user = buildUser();
        UserPreferences preferences = new UserPreferences();
        preferences.setUser(user);
        when(userPreferencesRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(userPreferencesMapper.createFor(user)).thenReturn(preferences);

        UserPreferences result = userPreferencesService.ensureDefaultsFor(user);

        assertThat(result.getUser().getId()).isEqualTo(USER_ID);
        verify(userPreferencesRepository).save(result);
    }

    @Test
    void ensureDefaultsFor_shouldReturnExisting_whenPresent() {
        UserPreferences existing = new UserPreferences();
        when(userPreferencesRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existing));

        UserPreferences result = userPreferencesService.ensureDefaultsFor(buildUser());

        assertThat(result).isEqualTo(existing);
        verify(userPreferencesRepository, never()).save(any());
    }

    @Test
    void deleteForUser_shouldDeleteExistingPreferences() {
        UserPreferences existing = new UserPreferences();
        when(userPreferencesRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existing));

        userPreferencesService.deleteForUser(USER_ID);

        verify(userPreferencesRepository).delete(existing);
    }

    @Test
    void getPreference_shouldReturnResponse_forExistingUser() {
        UserPreferences prefs = new UserPreferences();
        UserPreferencesResponse response = new UserPreferencesResponse(List.of("Action"), List.of("en"), false, true, true);

        when(userPreferencesRepository.findByUserId(USER_ID)).thenReturn(Optional.of(prefs));
        when(userPreferencesMapper.toResponse(prefs)).thenReturn(response);

        UserPreferencesResponse result = userPreferencesService.getPreference(USER_ID.toString());

        assertThat(result.favGenres()).contains("Action");
    }

    @Test
    void getPreference_shouldThrow_whenNotFound() {
        when(userPreferencesRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userPreferencesService.getPreference(USER_ID.toString()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getPreference_shouldThrow_forInvalidUUID() {
        assertThatThrownBy(() -> userPreferencesService.getPreference("invalid-uuid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updatePreferences_shouldCallMapperAndSave() {
        UserPreferences prefs = new UserPreferences();
        UpdateUserPreferencesRequest req = new UpdateUserPreferencesRequest(
                List.of("Comedy"), List.of("ar"), true, false, true);

        when(userPreferencesRepository.findByUserId(USER_ID)).thenReturn(Optional.of(prefs));

        userPreferencesService.updatePreferences(USER_ID.toString(), req);

        verify(userPreferencesMapper).updatePreferences(req, prefs);
        verify(userPreferencesRepository).save(prefs);
    }
}