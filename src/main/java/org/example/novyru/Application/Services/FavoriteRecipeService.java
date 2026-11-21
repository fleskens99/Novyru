package org.example.novyru.Application.Services;

import lombok.RequiredArgsConstructor;

import org.example.novyru.Domain.Interfaces.Repositories.IFavoriteRecipeRepository;
import org.example.novyru.Domain.Interfaces.Rules.IFavoriteRecipeRules;

import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class FavoriteRecipeService {

    private final IFavoriteRecipeRepository favoriteRepository;
    private final IFavoriteRecipeRules favoriteRules;
    private final ExternalRecipeService externalRecipeService;

    private final JsonMapper jsonMapper =
            JsonMapper.builder().build();

    // SAVE EXTERNAL RECIPE AS A FAVORITE
    public void addFavorite(int userId, int recipeId) {

        favoriteRules.validateId(userId, "User ID");
        favoriteRules.validateId(recipeId, "Recipe ID");

        // Step 1: Fetch the full recipe from the external API.
        Map<String, Object> recipe =
                externalRecipeService.getRecipeById(recipeId);

        // Step 2: Convert the recipe to JSON.
        String recipeJson =
                jsonMapper.writeValueAsString(recipe);

        // Step 3: Store it in MySQL for this user.
        boolean added = favoriteRepository.addFavorite(
                userId,
                recipeId,
                recipeJson
        );

        favoriteRules.requireAdded(
                added,
                userId,
                recipeId
        );
    }

    // REMOVE FAVORITE
    public void removeFavorite(int userId, int recipeId) {
        favoriteRules.validateId(userId, "User ID");
        favoriteRules.validateId(recipeId, "Recipe ID");

        boolean removed = favoriteRepository.removeFavorite(
                userId,
                recipeId
        );

        favoriteRules.requireRemoved(
                removed,
                userId,
                recipeId
        );
    }

    // CHECK FAVORITE
    public boolean isFavorite(int userId, int recipeId) {
        favoriteRules.validateId(userId, "User ID");
        favoriteRules.validateId(recipeId, "Recipe ID");

        return favoriteRepository.isFavorite(
                userId,
                recipeId
        );
    }

    // GET ONLY FAVORITE IDS
    public List<Integer> getFavoriteRecipeIds(int userId) {
        favoriteRules.validateId(userId, "User ID");

        return favoriteRepository
                .findFavoriteRecipeIdsByUserId(userId);
    }

    // GET FULL SAVED RECIPE OBJECTS
    public List<Object> getFavoriteRecipes(int userId) {
        favoriteRules.validateId(userId, "User ID");

        return favoriteRepository
                .findFavoriteRecipeJsonByUserId(userId)
                .stream()
                .map(json ->
                        jsonMapper.readValue(json, Object.class)
                )
                .toList();
    }
}
