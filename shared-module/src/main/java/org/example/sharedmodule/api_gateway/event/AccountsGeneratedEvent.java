package org.example.sharedmodule.api_gateway.event;

import java.util.List;
import java.util.UUID;

public record AccountsGeneratedEvent(UUID messageId, UUID orgHeadUserId, List<AccountInfo> accounts) {

    public record AccountInfo(UUID userId, String email, String password, String role) {}
}
