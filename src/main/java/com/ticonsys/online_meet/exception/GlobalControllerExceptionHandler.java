package com.ticonsys.online_meet.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityException(Exception ex) {
        log.error(ex.getClass().getName(), ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,LocalDateTime.now(),"Validation Error");
        return new ResponseEntity<>(apiError, apiError.httpStatus());
    }

    @ExceptionHandler(CustomSecurityException.class)
    public ResponseEntity<Object> handleCustomSecurityException(CustomSecurityException ex) {
        log.error(ex.getClass().getName(), ex.getMessage());

        ApiError apiError = new ApiError(ex.getHttpStatus(),LocalDateTime.now(),ex.getMessage());

        return new ResponseEntity<>(apiError, apiError.httpStatus());

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        log.error(ex.getMessage(), ex);
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,LocalDateTime.now(),ex.getMessage());

        return new ResponseEntity<>(apiError, apiError.httpStatus());

    }

    @ExceptionHandler(value = TokenNotFoundException.class)
    public ResponseEntity<Object> handleTokenNotFoundException(TokenNotFoundException ex, WebRequest request) {
        log.error(ex.getClass().getName(), ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND,LocalDateTime.now(),ex.getMessage());

        return new ResponseEntity<>(apiError, apiError.httpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        log.error(ex.getClass().getName(), ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,LocalDateTime.now(),ex.getMessage());

        return new ResponseEntity<>(apiError, apiError.httpStatus());

    }


    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex) {
        log.error(ex.getClass().getName(), ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN,LocalDateTime.now(),ex.getMessage());

        return new ResponseEntity<>(apiError, apiError.httpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {

        log.error(ex.getClass().getName(), ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList()));

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .filter(fieldError -> fieldError.getDefaultMessage() != null)
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,LocalDateTime.now(),ex.getMessage(),errors);

        return new ResponseEntity<>(apiError, apiError.httpStatus());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Object> handleSecurityException(SecurityException ex) {
        log.error(ex.getClass().getName(), ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN,LocalDateTime.now(),ex.getMessage());

        return new ResponseEntity<>(apiError, apiError.httpStatus());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Object> handleRunTimeException( RuntimeException ex) {
        log.error(ex.getClass().getName(), ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,LocalDateTime.now(),ex.getMessage());

        return new ResponseEntity<>(apiError, apiError.httpStatus());
    }
}
