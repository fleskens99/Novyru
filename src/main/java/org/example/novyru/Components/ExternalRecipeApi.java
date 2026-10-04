package org.example.novyru.Components;

import org.example.novyru.DTOs.RecipeDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ExternalRecipeApi {

    private final RestClient restClient;

    public ExternalRecipeApi(
            RestClient.Builder builder,
            @Value("${external.api.key}") String apiKey) {

        this.restClient = builder
                .baseUrl("https://external-api.com")
                .defaultHeader("Authorization", "Bearer " + apiKey)
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