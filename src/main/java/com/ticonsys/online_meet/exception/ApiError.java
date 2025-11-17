package com.ticonsys.online_meet.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @since 2025-11-17
 * @author Rashedul Islam
 * @version 1.0
 * @param httpStatus
 * @param createdAt
 * @param message
 * @param errors
 * @apiNote this class is used to return error response
 */
public record ApiError(
        HttpStatus httpStatus,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        LocalDateTime createdAt,
        String message,
        Map<String, String> errors
) {

    public ApiError(HttpStatus httpStatus, LocalDateTime createdAt, String message) {
        this(httpStatus,createdAt,message,null);
    }

    public ApiError(HttpStatus httpStatus, LocalDateTime createdAt, String message, Map<String, String> errors) {
        this.httpStatus = httpStatus;
        this.createdAt = createdAt;
        this.message = message;
        this.errors = errors;
    }
}
