package org.example.apigateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.apigateway.client.UserServiceClient;
import org.example.apigateway.dto.LoginRequest;
import org.example.apigateway.dto.RegisterRequest;
import org.example.apigateway.model.AuthCredential;
import org.example.apigateway.model.Role;
import org.example.apigateway.repository.AuthCredentialRepository;
import org.example.outbox.service.OutboxWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Testcontainers
@TestPropertySource(properties = {"EurekaLink=http://localhost:8761/eureka"})
class AuthControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired AuthCredentialRepository credentialRepository;

    @MockitoBean UserServiceClient userServiceClient;
    @MockitoBean StringRedisTemplate stringRedisTemplate;
    @MockitoBean OutboxWriter outboxWriter;

    private AuthCredential persistActiveUser() {
        AuthCredential credential = AuthCredential.builder()
            .email("test@test.com")
            .passwordHash(passwordEncoder.encode("password123"))
            .role(Role.USER)
            .isActive(true)
            .isVerified(true)
            .locked(false)
            .failedAttempts(0)
            .build();
        return credentialRepository.saveAndFlush(credential);
    }

    @Test
    void POST_register_shouldReturn201() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("new@test.com", "newuser12", "password123"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").isNotEmpty());
    }

    @Test
    void POST_register_shouldReturn400_forInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("not-an-email", "newuser12", "password123"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void POST_login_shouldReturn200_forValidCredentials() throws Exception {
        persistActiveUser();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("test@test.com", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access").isNotEmpty())
                .andExpect(jsonPath("$.refresh").isNotEmpty());
    }

    @Test
    void POST_login_shouldReturn401_forWrongPassword() throws Exception {
        persistActiveUser();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("test@test.com", "wrongpass"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void POST_login_shouldReturn400_forMissingEmail() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest());
    }
}
