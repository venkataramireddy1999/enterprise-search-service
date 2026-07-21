package com.nonsyncbobbal.enterprise_search_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            Logger.getLogger(GlobalExceptionHandler.class.getName());

    // Request body failed @Valid constraints (e.g. blank username) -> 400
    // with field-level messages instead of a generic exception string.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    // Duplicate username, and any other caller-input problem we raise
    // ourselves -> 400.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(
            IllegalArgumentException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    // Wrong username/password on login -> 401 (never reveal which part
    // was wrong).
    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<Map<String, String>> handleAuthFailure(
            AuthenticationException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid username or password"));
    }

    // Authenticated, but not allowed to touch this resource -> 403.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(
            AccessDeniedException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Access denied"));
    }

    // Document ID doesn't exist, or JWT names a user that no longer
    // exists -> 404.
    @ExceptionHandler({NoSuchElementException.class, UsernameNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(
            RuntimeException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Resource not found"));
    }

    // Anything unanticipated -> 500. Log the real exception server-side,
    // but never echo ex.getMessage() to the client - it can leak SQL,
    // stack, or config details.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(
            Exception ex
    ) {

        log.log(Level.SEVERE, "Unhandled exception", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred"));
    }
}
