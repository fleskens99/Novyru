package org.example.novyru.Services;

import org.example.novyru.Components.ExternalRecipeApi;
import org.example.novyru.DTOs.RecipeDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {

    private final ExternalRecipeApi externalRecipeApi;

    public RecipeService(ExternalRecipeApi externalRecipeApi) {
        this.externalRecipeApi = externalRecipeApi;
    }

    public List<RecipeDto> getRecipes() {

        return externalRecipeApi.getRecipes();
    }
}
