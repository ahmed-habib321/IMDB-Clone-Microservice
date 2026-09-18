package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import org.example.outbox.service.OutboxWriter;
import org.example.sharedmodule.api_gateway.dto.CreateUserRequest;
import org.example.sharedmodule.user_service.dto.UserDTO;
import org.example.sharedmodule.user_service.events.UserDeActivatedEvent;
import org.example.sharedmodule.user_service.events.UserEmailUpdatedEvent;
import org.example.sharedmodule.user_service.exception.EmailAlreadyExistsException;
import org.example.sharedmodule.user_service.exception.UserNotFoundException;
import org.example.sharedmodule.utils.UUIDUtils;
import org.example.userservice.dto.Request.UpdateUserRequest;
import org.example.userservice.dto.Request.UserRegisterRequest;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.model.User;
import org.example.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.example.sharedmodule.Constants.TOPIC_NAMES.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileService userProfileService;
    private final UserPreferencesService userPreferencesService;
    private final UserMapper userMapper;
    private final OutboxWriter outboxWriter;

    @Transactional
    public UUID register(UserRegisterRequest userRegisterRequest) {
        if (userRepository.existsByEmail(userRegisterRequest.email())) throw new EmailAlreadyExistsException("Email already in use");
        return createAccount(userMapper.createUser(null,userRegisterRequest.email(), userRegisterRequest.username())).getId();
    }

    @Transactional
    public UUID register(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) throw new EmailAlreadyExistsException("Email already in use");
        return createAccount(userMapper.createUser(request.userId(),request.email(),request.username())).getId();
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(String id) {
        UUID uuid = UUIDUtils.parse(id);
        User user = userRepository.findUserById(uuid).orElseThrow(() -> new UserNotFoundException(uuid));
        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDTO findOrCreateOAuthUser(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseGet(() -> createAccount(userMapper.createUser(null,email, email.split("@")[0])));
        return userMapper.toDTO(user);
    }

    @Transactional
    public void updateUser(String id, UpdateUserRequest updateUserRequest) {
        UUID uuid = UUIDUtils.parse(id);
        User user = userRepository.findUserById(uuid).orElseThrow(() -> new UserNotFoundException(uuid));

        boolean emailChanged = updateUserRequest.email() != null && !updateUserRequest.email().equals(user.getEmail());
        userMapper.updateUser(updateUserRequest, user);

        if (emailChanged) {
            outboxWriter.save(new UserEmailUpdatedEvent(uuid, user.getEmail()), USER_EMAIL_UPDATED, uuid.toString());
        }
    }

    @Transactional
    public void deleteUser(String id) {
        UUID uuid = UUIDUtils.parse(id);
        User user = userRepository.findUserById(uuid).orElseThrow(() -> new UserNotFoundException(uuid));
        userProfileService.deleteForUser(uuid);
        userPreferencesService.deleteForUser(uuid);
        userRepository.delete(user);
        outboxWriter.save(new UserDeActivatedEvent(uuid, Instant.now()), USER_DEACTIVATED, uuid.toString());
    }

    private User createAccount(User user) {
        userRepository.save(user);
        userProfileService.ensureDefaultsFor(user);
        userPreferencesService.ensureDefaultsFor(user);
        return user;
    }

}