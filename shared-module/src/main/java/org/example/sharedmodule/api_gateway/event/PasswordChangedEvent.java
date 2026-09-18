package org.example.sharedmodule.api_gateway.event;

import java.util.UUID;

public record PasswordChangedEvent(UUID messageId, UUID userId) {}
