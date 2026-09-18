package org.example.apigateway.client;

import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.example.sharedmodule.api_gateway.dto.CreateUserRequest;
import org.example.sharedmodule.general_exceptions.RemoteServiceUnavailableException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.util.UUID;

@Slf4j
@Component
public class UserServiceClientFallbackFactory implements FallbackFactory<UserServiceClient> {

    private static String resolveReason(Throwable cause) {
        Throwable root = cause;
        while (root.getCause() != null) root = root.getCause();

        String msg = root.getMessage();
        if (msg == null) return "unknown error";

        if (msg.contains("does not contain an instance"))
            return "no instances available in service discovery — user-service may be down or not registered with Eureka";

        if (msg.contains("Connection refused"))
            return "connection refused — user-service is not accepting connections";

        if (root instanceof ConnectException)
            return "cannot connect to user-service — the service may be down";

        if (root instanceof RetryableException)
            return "request to user-service failed after retries — " + msg;

        if (msg.contains("timeout") || msg.contains("timed out") || msg.contains("TimeOut"))
            return "request to user-service timed out — the service may be overloaded";

        return msg;
    }

    @Override
    public UserServiceClient create(Throwable cause) {
        String reason = resolveReason(cause);
        log.error("user-service call failed: {}", reason);

        return new UserServiceClient() {
            @Override
            public UUID registerUser(CreateUserRequest request) {
                throw new RemoteServiceUnavailableException("user-service", "registration");
            }
        };
    }
}