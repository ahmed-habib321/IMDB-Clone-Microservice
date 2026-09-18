package org.example.sharedmodule.api_gateway.event;

import java.util.UUID;

public record PasswordResetEvent(UUID messageId, UUID userId, String token) {}
