package org.example.sharedmodule.search_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.sharedmodule.general_exceptions.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class SearchExceptionHandler {

    @ExceptionHandler(SearchUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleSearchUnavailable(SearchUnavailableException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.of(503, "Service Unavailable", "Search is temporarily unavailable — please try again later"));
    }
}