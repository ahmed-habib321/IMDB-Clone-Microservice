package org.example.userservice.service;

import org.example.outbox.service.OutboxWriter;
import org.example.sharedmodule.user_service.dto.UserDTO;
import org.example.sharedmodule.user_service.events.UserDeActivatedEvent;
import org.example.sharedmodule.user_service.exception.EmailAlreadyExistsException;
import org.example.sharedmodule.user_service.exception.UserNotFoundException;
import org.example.userservice.dto.Request.UserRegisterRequest;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.model.User;
import org.example.userservice.repository.UserRepository;
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
import static org.mockito.Mockito.*;
import static org.example.sharedmodule.Constants.TOPIC_NAMES.USER_DEACTIVATED;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserProfileService userProfileService;
    @Mock UserPreferencesService userPreferencesService;
    @Mock UserMapper userMapper;
    @Mock OutboxWriter outboxWriter;

    @InjectMocks UserService userService;

    private static final UUID USER_ID = UUID.randomUUID();

    private User buildUser() {
        return User.builder()
                .id(USER_ID)
                .email("test@test.com")
                .username("testuser")
                .build();
    }

    @Test
    void register_shouldSaveAndEnsureDefaults() {
        UserRegisterRequest req = new UserRegisterRequest("test@test.com", "testuser", "encodedpw");
        User savedUser = buildUser();

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(userMapper.createUser(null,req.email(),req.username())).thenReturn(savedUser);
        when(userRepository.save(savedUser)).thenReturn(savedUser);

        UUID result = userService.register(req);

        assertThat(result).isEqualTo(USER_ID);
        verify(userRepository).save(savedUser);
        verify(userProfileService).ensureDefaultsFor(savedUser);
        verify(userPreferencesService).ensureDefaultsFor(savedUser);
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(new UserRegisterRequest("test@test.com", "u", "p")))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_shouldReturnDTO_forExistingUser() {
        User user = buildUser();
        UserDTO dto = UserDTO.builder().id(USER_ID).email("test@test.com").build();

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(dto);

        UserDTO result = userService.getUserById(USER_ID.toString());

        assertThat(result.id()).isEqualTo(USER_ID);
    }

    @Test
    void getUserById_shouldThrow_forNonExistentUser() {
        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(USER_ID.toString()))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getUserById_shouldThrow_forInvalidUUID() {
        assertThatThrownBy(() -> userService.getUserById("not-a-uuid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid UUID format");
    }

    @Test
    void deleteUser_shouldDeleteAndPublishEvent() {
        User user = buildUser();
        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));

        userService.deleteUser(USER_ID.toString());

        verify(userProfileService).deleteForUser(USER_ID);
        verify(userPreferencesService).deleteForUser(USER_ID);
        verify(userRepository).delete(user);
        verify(outboxWriter).save(any(UserDeActivatedEvent.class), eq(USER_DEACTIVATED), anyString());
    }

    @Test
    void deleteUser_shouldThrow_forNonExistentUser() {
        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(USER_ID.toString()))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).delete(any());
    }
}
