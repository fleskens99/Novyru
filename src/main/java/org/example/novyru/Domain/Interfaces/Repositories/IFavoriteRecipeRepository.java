
package org.example.novyru.Domain.Interfaces.Repositories;

import java.util.List;

public interface IFavoriteRecipeRepository {

    boolean addFavorite(
            int userId,
            int recipeId,
            String recipeJson
    );

    boolean removeFavorite(int userId, int recipeId);

    boolean isFavorite(int userId, int recipeId);

    List<Integer> findFavoriteRecipeIdsByUserId(int userId);

    List<String> findFavoriteRecipeJsonByUserId(int userId);
}
