package org.example.sharedmodule.general_exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(String message, HttpStatus Status) {
        this.status = Status;
        super(message);
    }
}
