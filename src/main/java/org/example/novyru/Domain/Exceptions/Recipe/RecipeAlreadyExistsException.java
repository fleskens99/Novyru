package org.example.novyru.Domain.Exceptions.Recipe;

public class RecipeAlreadyExistsException extends RuntimeException {

    public RecipeAlreadyExistsException(String message) {
        super("Recipe is already in favorites. " + message);
    }
}