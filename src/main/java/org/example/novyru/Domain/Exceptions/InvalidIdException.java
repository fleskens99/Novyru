package org.example.novyru.Domain.Exceptions;

public class InvalidIdException extends RuntimeException {

    public InvalidIdException(String fieldName) {
        super(fieldName + " must be greater than zero.");
    }
}
