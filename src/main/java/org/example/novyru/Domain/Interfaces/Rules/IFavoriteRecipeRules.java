package org.example.novyru.Domain.Interfaces.Rules;

public interface IFavoriteRecipeRules {

    void validateId(int id, String fieldName);

    void requireAdded(
            boolean added,
            int userId,
            int recipeId
    );

    public void requireRemoved(
            boolean removed,
            int userId,
            int recipeId
    );
}
