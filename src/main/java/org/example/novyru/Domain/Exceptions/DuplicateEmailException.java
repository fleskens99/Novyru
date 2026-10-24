package org.example.novyru.Domain.Exceptions;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super("Email is already registered.");
    }
}
