package org.example.sharedmodule.api_gateway.event;

import java.util.UUID;

public record EmailVerificationEvent(UUID messageId, UUID userId, String email, String token) {}
