package org.example.novyru.Components;

import org.example.novyru.DTOs.RecipeDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ExternalRecipeApi {

    private final RestClient restClient;

    public ExternalRecipeApi() {
        this.restClient = RestClient.builder()
                .baseUrl("https://recipeapi.io/api/v1/recipes?lang=en")
                .build();
    }

    public List<RecipeDto> getRecipes() {
        return restClient
                .get()
                .uri("/recipes")
                .retrieve()
                .body(new ParameterizedTypeReference<List<RecipeDto>>() {});
    }
}