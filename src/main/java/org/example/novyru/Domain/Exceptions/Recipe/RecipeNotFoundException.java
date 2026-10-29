package org.example.novyru.Domain.Exceptions.Recipe;

public class RecipeNotFoundException extends RuntimeException {

    public RecipeNotFoundException(String message) {
        super("Recipe was not found in favorites. " + message);
    }
}