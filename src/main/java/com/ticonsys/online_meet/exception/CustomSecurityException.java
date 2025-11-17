package com.ticonsys.online_meet.exception;

import org.springframework.http.HttpStatus;

public final class CustomSecurityException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;

    public CustomSecurityException(String message) {
        super(message);
        this.message = message;
        this.httpStatus = null;
    }

    public CustomSecurityException(String message, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
