package org.example.userservice.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.sharedmodule.Validation.NullOrNotBlank;

public record UpdateUserRequest(

        @Email(message = "Must be a valid email address")
        @Size(max = 255, message = "Email must be at most 255 characters")
        @NullOrNotBlank
        String email,

        @Pattern(regexp = "^[A-Za-z].*", message = "username must start with alphabet")
        @Size(min = 5, max = 50, message = "username must be between 5 and 50 characters")
        @NullOrNotBlank
        String username


) {}