package org.example.novyru.Application.Exceptions;

import org.example.novyru.Domain.Exceptions.DuplicateEmailException;
import org.example.novyru.Domain.Exceptions.InvalidCredentialsException;
import org.example.novyru.Domain.Exceptions.InvalidRegistrationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    // Standard JSON error response
    public record ApiError(String message) {}

    // 409 - Email already exists
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiError> handleDuplicateEmail(
            DuplicateEmailException e
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError(e.getMessage()));
    }

    // 401 - Login credentials are incorrect
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(
            InvalidCredentialsException e
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError("Invalid email or password."));
    }

    // 400 - Registration information is invalid
    @ExceptionHandler(InvalidRegistrationException.class)
    public ResponseEntity<ApiError> handleInvalidRegistration(
            InvalidRegistrationException e
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(e.getMessage()));
    }
}
