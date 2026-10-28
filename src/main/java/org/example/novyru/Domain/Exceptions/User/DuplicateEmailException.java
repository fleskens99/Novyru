package org.example.novyru.Domain.Exceptions.User;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super("Email is already registered. " + message);
    }
}