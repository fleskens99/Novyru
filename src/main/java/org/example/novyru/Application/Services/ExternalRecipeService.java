package org.example.novyru.Application.Services;

import org.example.novyru.Application.DTOs.RecipeDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.example.novyru.Application.DTOs.RecipeResponseDto;

import java.util.List;

@Component
public class ExternalRecipeService {

    private final RestClient restClient;

    public ExternalRecipeService(
            @Value("${external.api.key}") String apiKey) {

        this.restClient = RestClient.builder()
                .baseUrl("https://recipeapi.io/api/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public List<RecipeDto> getRecipes() {

        RecipeResponseDto response = restClient
                .get()
                .uri("/recipes?lang=en")
                .retrieve()
                .body(RecipeResponseDto.class);

        return response.getData();
    }
}