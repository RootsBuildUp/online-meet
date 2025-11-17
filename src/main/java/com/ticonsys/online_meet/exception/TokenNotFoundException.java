package com.ticonsys.online_meet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String message) {
        super(String.format("Failed: %s", message));
    }

    public TokenNotFoundException(String token, String message) {
        super(String.format("Failed for [%s]: %s", token, message));
    }
}
