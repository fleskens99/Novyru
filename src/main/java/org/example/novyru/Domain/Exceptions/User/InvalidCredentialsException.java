package org.example.novyru.Domain.Exceptions.User;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super("Invalid email or password. " + message);
    }
}