package org.example.novyru.Application.Services;

import org.example.novyru.Application.DTOs.RecipeDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {

    private final ExternalRecipeService externalRecipeApi;

    public RecipeService(ExternalRecipeService externalRecipeApi) {
        this.externalRecipeApi = externalRecipeApi;
    }

    public List<RecipeDto> getRecipes() {

        return externalRecipeApi.getRecipes();
    }
}
