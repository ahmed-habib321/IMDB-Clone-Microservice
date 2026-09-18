package org.example.userservice.service;

import org.example.outbox.service.OutboxWriter;
import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.example.sharedmodule.user_service.events.UserProfileUpdatedEvent;
import org.example.userservice.dto.Request.UpdateUserProfileRequest;
import org.example.userservice.dto.Response.UserProfileResponse;
import org.example.userservice.mapper.UserProfileMapper;
import org.example.userservice.model.User;
import org.example.userservice.model.UserProfile;
import org.example.userservice.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.example.sharedmodule.Constants.TOPIC_NAMES.USER_PROFILE_UPDATED;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock UserProfileRepository userProfileRepository;
    @Mock UserProfileMapper userProfileMapper;
    @Mock OutboxWriter outboxWriter;

    @InjectMocks UserProfileService userProfileService;

    private static final UUID USER_ID = UUID.randomUUID();

    private User buildUser() {
        User user = new User();
        user.setId(USER_ID);
        return user;
    }

    @Test
    void ensureDefaultsFor_shouldCreateAndPersist_whenMissing() {
        User user = buildUser();
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        when(userProfileRepository.findUserProfileByUserId(USER_ID)).thenReturn(Optional.empty());
        when(userProfileMapper.createFor(user)).thenReturn(profile);

        UserProfile result = userProfileService.ensureDefaultsFor(user);

        assertThat(result.getUser().getId()).isEqualTo(USER_ID);
        verify(userProfileRepository).save(result);
    }

    @Test
    void ensureDefaultsFor_shouldReturnExisting_whenPresent() {
        UserProfile existing = new UserProfile();
        when(userProfileRepository.findUserProfileByUserId(USER_ID)).thenReturn(Optional.of(existing));

        UserProfile result = userProfileService.ensureDefaultsFor(buildUser());

        assertThat(result).isEqualTo(existing);
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void deleteForUser_shouldDeleteExistingProfile() {
        UserProfile existing = new UserProfile();
        when(userProfileRepository.findUserProfileByUserId(USER_ID)).thenReturn(Optional.of(existing));

        userProfileService.deleteForUser(USER_ID);

        verify(userProfileRepository).delete(existing);
    }

    @Test
    void getProfile_shouldReturnResponse_forExistingUser() {
        UserProfile profile = new UserProfile();
        profile.setDisplayName("Ahmed");
        UserProfileResponse response = new UserProfileResponse("Ahmed", null, null, null, null, 0, 0);

        when(userProfileRepository.findUserProfileByUserId(USER_ID)).thenReturn(Optional.of(profile));
        when(userProfileMapper.toResponse(profile)).thenReturn(response);

        UserProfileResponse result = userProfileService.getProfile(USER_ID.toString());

        assertThat(result.displayName()).isEqualTo("Ahmed");
    }

    @Test
    void getProfile_shouldThrow_whenNotFound() {
        when(userProfileRepository.findUserProfileByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.getProfile(USER_ID.toString()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateProfile_shouldSaveAndPublishEvent() {
        UserProfile profile = new UserProfile();
        profile.setDisplayName("OldName");
        UpdateUserProfileRequest req = new UpdateUserProfileRequest(
                "NewName", null, null, null, null, null);

        when(userProfileRepository.findUserProfileByUserId(USER_ID)).thenReturn(Optional.of(profile));

        userProfileService.updateProfile(USER_ID.toString(), req);

        verify(userProfileMapper).updateProfile(req, profile);
        verify(userProfileRepository).save(profile);
        verify(outboxWriter).save(any(UserProfileUpdatedEvent.class), eq(USER_PROFILE_UPDATED), anyString());
    }

    @Test
    void updateProfile_shouldThrow_forInvalidUUID() {
        assertThatThrownBy(() -> userProfileService.updateProfile("bad-uuid", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
