package org.example.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.outbox.service.OutboxWriter;
import org.example.userservice.dto.Request.UserRegisterRequest;
import org.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Testcontainers
class UserControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");


    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @MockitoBean OutboxWriter outboxWriter;  // avoid Kafka / outbox dependency

    static final String API_URL = "/api/v1/private/user";

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Test
    void POST_register_shouldReturn201_andPersistedUser() throws Exception {
        UserRegisterRequest req = new UserRegisterRequest("alice@test.com", "aliceuser", "$2a$12$encoded");

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.matchesRegex(
                        "\"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\"")));
    }

    @Test
    void POST_register_shouldReturn400_forDuplicateEmail() throws Exception {
        UserRegisterRequest req = new UserRegisterRequest("duplicate@test.com", "dupuser", "$2a$12$encoded");

        // First registration
        mockMvc.perform(post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        // Duplicate
        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email Already Exists"));
    }

    @Test
    void GET_userById_shouldReturn404_forNonExistentUser() throws Exception {
        mockMvc.perform(get(API_URL + "/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void GET_userByEmail_shouldReturn404_forUnknownEmail() throws Exception {
        mockMvc.perform(get(API_URL + "/by-email")
                        .param("email", "nobody@test.com"))
                .andExpect(status().isNotFound());
    }
}