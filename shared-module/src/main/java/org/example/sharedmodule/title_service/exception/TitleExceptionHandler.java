package org.example.sharedmodule.title_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.sharedmodule.general_exceptions.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class TitleExceptionHandler {

    @ExceptionHandler(TitleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTitleNotFound(TitleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(404, "Not Found", ex.getMessage()));
    }

}
