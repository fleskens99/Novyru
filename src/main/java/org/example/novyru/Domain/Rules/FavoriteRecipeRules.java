package org.example.novyru.Domain.Rules;

import org.example.novyru.Domain.Exceptions.InvalidIdException;
import org.example.novyru.Domain.Exceptions.Recipe.RecipeAlreadyExistsException;
import org.example.novyru.Domain.Exceptions.Recipe.RecipeNotFoundException;
import org.example.novyru.Domain.Interfaces.Rules.IFavoriteRecipeRules;

public final class FavoriteRecipeRules implements IFavoriteRecipeRules {

    private FavoriteRecipeRules(){
    }

    // GENERAL ID VALIDATION
    public void validateId(int id, String fieldName) {

        if (id <= 0) {
            throw new InvalidIdException(fieldName);
        }
    }

    // FAVORITE ALREADY EXISTS
    public void requireAdded(
            boolean added,
            int userId,
            int recipeId
    ) {
        if (!added) {
            throw new RecipeAlreadyExistsException(
                    "User ID: " + userId
                            + ", Recipe ID: " + recipeId
            );
        }
    }

    // FAVORITE NOT FOUND
    public void requireRemoved(
            boolean removed,
            int userId,
            int recipeId
    ) {
        if (!removed) {
            throw new RecipeNotFoundException(
                    "User ID: " + userId
                            + ", Recipe ID: " + recipeId
            );
        }
    }
}
