
package org.example.novyru.Application.Services;

import org.example.novyru.Application.DTOs.RecipeDto;
import org.example.novyru.Application.DTOs.RecipeResponseDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExternalRecipeService {

    private final RestClient restClient;

    public ExternalRecipeService(
            @Value("${external.api.key}") String apiKey
    ) {
        this.restClient = RestClient.builder()
                .baseUrl("https://recipeapi.io/api/v1")
                .defaultHeader(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .build();
    }

    // Existing method: list recipes on the homepage
    public List<RecipeDto> getRecipes() {
        RecipeResponseDto response = restClient
                .get()
                .uri("/recipes?lang=en")
                .retrieve()
                .body(RecipeResponseDto.class);

        if (response == null || response.getData() == null) {
            return List.of();
        }

        return response.getData();
    }

    // New method: retrieve one COMPLETE recipe by its ID
    public Map<String, Object> getRecipeById(int recipeId) {
        if (recipeId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid recipe ID: " + recipeId
            );
        }

        try {
            Map<?, ?> response = restClient
                    .get()
                    .uri("/recipes/{id}?lang=en", recipeId)
                    .retrieve()
                    .body(Map.class);

            if (response == null ||
                    !(response.get("data") instanceof Map<?, ?> data)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "The external API returned invalid recipe data."
                );
            }

            Map<String, Object> recipe = new LinkedHashMap<>();

            data.forEach((key, value) -> {
                if (key instanceof String name) {
                    recipe.put(name, value);
                }
            });

            Object externalId = recipe.get("id");

            if (!(externalId instanceof Number number)
                    || number.longValue() != recipeId) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "The external API returned a mismatched recipe."
                );
            }

            return recipe;

        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recipe not found in external API.",
                        e
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Could not retrieve the recipe from the external API.",
                    e
            );
        }
    }
}
