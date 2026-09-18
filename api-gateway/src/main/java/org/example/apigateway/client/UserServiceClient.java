package org.example.apigateway.client;

import org.example.sharedmodule.api_gateway.dto.CreateUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "user-service", path = "/api/v1/private/user", fallbackFactory = UserServiceClientFallbackFactory.class)
public interface UserServiceClient {

    @PostMapping("/from-credential")
    UUID registerUser(@RequestBody CreateUserRequest request);
}