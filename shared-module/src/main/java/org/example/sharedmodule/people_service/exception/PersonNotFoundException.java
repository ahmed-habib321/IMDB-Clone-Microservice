package org.example.sharedmodule.people_service.exception;

import java.util.UUID;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(UUID id) {
        super("Person not found with id: " + id);
    }
    public PersonNotFoundException(String slug) {
        super("Person not found with slug: " + slug);
    }
}