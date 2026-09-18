package org.example.sharedmodule.general_exceptions;

import org.springframework.http.HttpStatus;

public class RemoteServiceUnavailableException extends RuntimeException {

    private final String service;
    private final String operation;

    public RemoteServiceUnavailableException(String service, String operation) {
        super("Remote service '" + service + "' is unavailable while performing: " + operation);
        this.service = service;
        this.operation = operation;
    }

    public HttpStatus getStatus() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

    public String getService() {
        return service;
    }

    public String getOperation() {
        return operation;
    }
}
