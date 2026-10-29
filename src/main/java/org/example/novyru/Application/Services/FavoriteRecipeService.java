package org.example.novyru.Application.Services;

import lombok.RequiredArgsConstructor;

import org.example.novyru.Domain.Interfaces.Repositories.IFavoriteRecipeRepository;
import org.example.novyru.Domain.Interfaces.Rules.IFavoriteRecipeRules;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FavoriteRecipeService {

    private final IFavoriteRecipeRepository favoriteRepository;
    private final IFavoriteRecipeRules favoriteRules;

    // ADD FAVORITE
    public void addFavorite(int userId, int recipeId) {

        favoriteRules.validateId(userId, "User ID");
        favoriteRules.validateId(recipeId, "Recipe ID");

        boolean added = favoriteRepository.addFavorite(userId, recipeId);

        favoriteRules.requireAdded(added, userId, recipeId);
    }

    // REMOVE FAVORITE
    public void removeFavorite(int userId, int recipeId) {

        favoriteRules.validateId(userId, "User ID");
        favoriteRules.validateId(recipeId, "Recipe ID");

        boolean removed = favoriteRepository.removeFavorite(userId, recipeId);

        favoriteRules.requireRemoved(removed, userId, recipeId);
    }

    // CHECK IF FAVORITED
    public boolean isFavorite(int userId, int recipeId) {

        favoriteRules.validateId(userId, "User ID");
        favoriteRules.validateId(recipeId, "Recipe ID");

        return favoriteRepository.isFavorite(userId, recipeId);
    }

    // GET ALL FAVORITE RECIPE IDS
    public List<Integer> getFavoriteRecipeIds(int userId) {

        favoriteRules.validateId(userId, "User ID");

        return favoriteRepository.findFavoriteRecipeIdsByUserId(userId);
    }
}
