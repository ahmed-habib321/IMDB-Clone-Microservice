package org.example.apigateway.controller;

import org.example.apigateway.dto.AuthResponse;
import org.example.apigateway.dto.ChangePasswordRequest;
import org.example.apigateway.dto.LoginRequest;
import org.example.apigateway.dto.RegisterRequest;
import org.example.apigateway.dto.ResetPasswordRequest;
import org.example.apigateway.dto.VerifyEmailRequest;
import org.example.apigateway.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private final AuthService authService = mock(AuthService.class);
    private final AuthController controller = new AuthController(authService);

    @Test
    void register_shouldReturnCreatedAndDelegate() {
        ResponseEntity<Void> response = controller.register(new RegisterRequest("test@test.com", "TestUser", "password123"));

        org.assertj.core.api.Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void login_shouldReturnAuthResponse() {
        when(authService.login(any(LoginRequest.class))).thenReturn(new AuthResponse("access", "refresh"));

        ResponseEntity<AuthResponse> response = controller.login(new LoginRequest("test@test.com", "password"));

        org.assertj.core.api.Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        org.assertj.core.api.Assertions.assertThat(response.getBody()).isEqualTo(new AuthResponse("access", "refresh"));
    }

    @Test
    void logout_shouldReturnNoContent() {
        ResponseEntity<Void> response = controller.logout("Bearer token");

        org.assertj.core.api.Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(authService).logout("token");
    }

    @Test
    void triggerVerificationEmail_shouldReturnOk() {
        ResponseEntity<Void> response = controller.triggerVerificationEmail("test@test.com");

        org.assertj.core.api.Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(authService).triggerVerificationEmail("test@test.com");
    }

    @Test
    void verifyEmail_shouldReturnOk() {
        ResponseEntity<Void> response = controller.verifyEmail(new VerifyEmailRequest("123456"));

        org.assertj.core.api.Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(authService).verifyEmail("123456");
    }

    @Test
    void triggerPasswordForgot_shouldReturnOk() {
        ResponseEntity<Void> response = controller.triggerPasswordForgot("test@test.com");

        org.assertj.core.api.Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(authService).triggerPasswordReset("test@test.com");
    }

    @Test
    void resetPassword_shouldReturnOk() {
        ResponseEntity<Void> response = controller.resetPassword(new ResetPasswordRequest("654321", "newPassword123"));

        org.assertj.core.api.Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(authService).resetPassword(any(ResetPasswordRequest.class));
    }

    @Test
    void changePassword_shouldReturnNoContent() {
        UUID userId = UUID.randomUUID();
        ResponseEntity<Void> response = controller.changePassword(userId.toString(), new ChangePasswordRequest("currentPass", "newPass123"));

        org.assertj.core.api.Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(authService).changePassword(org.mockito.ArgumentMatchers.eq(userId), any(ChangePasswordRequest.class));
    }

    @Test
    void deactivateAccount_shouldReturnNoContent() {
        UUID userId = UUID.randomUUID();
        ResponseEntity<Void> response = controller.deactivateAccount(userId.toString());

        org.assertj.core.api.Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(authService).deactivateAccount(userId);
    }
}
