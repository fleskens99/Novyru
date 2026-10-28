package org.example.novyru.Domain.Interfaces.Repositories;

import java.util.List;

public interface IFavoriteRecipeRepository  {
    public boolean addFavorite(int userId, int recipeId);
    public boolean removeFavorite(int userId, int recipeId);
    public boolean isFavorite(int userId, int recipeId);
    public List<Integer> findFavoriteRecipeIdsByUserId(int userId);
}
