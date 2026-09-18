package org.example.sharedmodule.user_service.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String Email) {
        super("The email already exists: " + Email);
    }
}
