package org.example.outbox.kafka;

public class OutboxKafkaException extends RuntimeException {

    public OutboxKafkaException(String message, Throwable cause) {
        super(message, cause);
    }
}
