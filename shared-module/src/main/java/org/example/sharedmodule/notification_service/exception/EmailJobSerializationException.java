package org.example.sharedmodule.notification_service.exception;

public class EmailJobSerializationException extends RuntimeException {

    public EmailJobSerializationException(Throwable cause) {
        super("Failed to serialize email job variables", cause);
    }
}