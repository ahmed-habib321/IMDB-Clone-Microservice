package org.example.userservice.service;


import lombok.RequiredArgsConstructor;
import org.example.sharedmodule.general_exceptions.ResourceNotFoundException;
import org.example.sharedmodule.utils.UUIDUtils;
import org.example.userservice.dto.Request.UpdateUserPreferencesRequest;
import org.example.userservice.dto.Response.UserPreferencesResponse;
import org.example.userservice.mapper.UserPreferencesMapper;
import org.example.userservice.model.User;
import org.example.userservice.model.UserPreferences;
import org.example.userservice.repository.UserPreferencesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPreferencesService {


    private final UserPreferencesRepository userPreferencesRepository;
    private final UserPreferencesMapper userPreferencesMapper;

    public UserPreferences ensureDefaultsFor(User user) {
        return userPreferencesRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserPreferences preferences = userPreferencesMapper.createFor(user);
                    userPreferencesRepository.save(preferences);
                    return preferences;
                });
    }

    public void deleteForUser(UUID userId) {
        userPreferencesRepository.findByUserId(userId).ifPresent(userPreferencesRepository::delete);
    }

    @Transactional(readOnly = true)
    public UserPreferencesResponse getPreference(String id) {
        UUID uuid = UUIDUtils.parse(id);
        UserPreferences prefs = userPreferencesRepository.findByUserId(uuid).orElseThrow(() -> new ResourceNotFoundException("UserPreferences", id));
        return userPreferencesMapper.toResponse(prefs);

    }

    @Transactional
    public void updatePreferences(String id, UpdateUserPreferencesRequest updateUserPreferencesRequest) {
        UUID uuid = UUIDUtils.parse(id);

        UserPreferences prefs = userPreferencesRepository.findByUserId(uuid).orElseThrow(() -> new ResourceNotFoundException("UserPreferences", id));
        userPreferencesMapper.updatePreferences(updateUserPreferencesRequest, prefs);
        userPreferencesRepository.save(prefs);
    }
}
