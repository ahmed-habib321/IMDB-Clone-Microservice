package org.example.sharedmodule.api_gateway.exception;

public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException() {
        super("Rate limit exceeded");
    }

    public RateLimitExceededException(String message) {
        super(message);
    }
}
