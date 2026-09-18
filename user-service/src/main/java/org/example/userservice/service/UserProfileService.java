package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import org.example.outbox.service.OutboxWriter;
import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.example.sharedmodule.user_service.events.UserProfileUpdatedEvent;
import org.example.sharedmodule.utils.UUIDUtils;
import org.example.userservice.dto.Request.UpdateUserProfileRequest;
import org.example.userservice.dto.Response.UserProfileResponse;
import org.example.userservice.mapper.UserProfileMapper;
import org.example.userservice.model.User;
import org.example.userservice.model.UserProfile;
import org.example.userservice.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.example.sharedmodule.Constants.TOPIC_NAMES.USER_PROFILE_UPDATED;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final OutboxWriter outboxWriter;

    public UserProfile ensureDefaultsFor(User user) {
        return userProfileRepository.findUserProfileByUserId(user.getId())
                .orElseGet(() -> {
                    UserProfile profile = userProfileMapper.createFor(user);
                    userProfileRepository.save(profile);
                    return profile;
                });
    }

    public void deleteForUser(UUID userId) {
        userProfileRepository.findUserProfileByUserId(userId).ifPresent(userProfileRepository::delete);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String id) {
        UUID uuid = UUIDUtils.parse(id);
        UserProfile prof = userProfileRepository.findUserProfileByUserId(uuid).orElseThrow(() -> new ResourceNotFoundException("UserProfile", id));
        return userProfileMapper.toResponse(prof);
    }
    @Transactional
    public void updateProfile(String id, UpdateUserProfileRequest updateUserProfileRequest) {
        UUID uuid = UUIDUtils.parse(id);
        UserProfile prof = userProfileRepository.findUserProfileByUserId(uuid).orElseThrow(() -> new ResourceNotFoundException("UserProfile", id));
        userProfileMapper.updateProfile(updateUserProfileRequest, prof);
        userProfileRepository.save(prof);
        outboxWriter.save(new UserProfileUpdatedEvent(
                uuid, prof.getDisplayName(), prof.getAvatarUrl(), Instant.now()), USER_PROFILE_UPDATED, uuid.toString());
    }
}
