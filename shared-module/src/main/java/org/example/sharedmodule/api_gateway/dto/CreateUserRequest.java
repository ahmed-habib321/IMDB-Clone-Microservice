package org.example.sharedmodule.api_gateway.dto;

import java.util.UUID;

public record CreateUserRequest(UUID userId, String email, String username, String password) {}
