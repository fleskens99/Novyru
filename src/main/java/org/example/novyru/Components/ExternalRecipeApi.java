package org.example.novyru.Components;

import org.example.novyru.DTOs.RecipeDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.example.novyru.DTOs.RecipeResponse;

import java.util.List;

@Component
public class ExternalRecipeApi {

    private final RestClient restClient;

    public ExternalRecipeApi(
            @Value("${external.api.key}") String apiKey) {

        this.restClient = RestClient.builder()
                .baseUrl("https://recipeapi.io/api/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public List<RecipeDto> getRecipes() {

        RecipeResponse response = restClient
                .get()
                .uri("/recipes?lang=en")
                .retrieve()
                .body(RecipeResponse.class);

        return response.getData();
    }
}