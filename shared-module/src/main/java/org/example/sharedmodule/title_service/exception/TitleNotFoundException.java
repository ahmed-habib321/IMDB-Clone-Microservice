package org.example.sharedmodule.title_service.exception;

import java.util.UUID;

public class TitleNotFoundException extends RuntimeException {
    public TitleNotFoundException(UUID id) {
        super("Title not found with id: " + id);
    }
    public TitleNotFoundException(String message) {
        super(message);
    }
}
