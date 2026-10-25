
package org.example.novyru;

import org.example.novyru.Application.Services.ExternalRecipeService;
import org.example.novyru.Application.DTOs.RecipeDto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class ExternalRecipeApiTest {

    @Test
    @EnabledIfEnvironmentVariable(
            named = "RECIPE_API_KEY",
            matches = ".+"
    )
    void testGetRecipes() {

        String apiKey = System.getenv("RECIPE_API_KEY");

        ExternalRecipeService service =
                new ExternalRecipeService(apiKey);

        List<RecipeDto> recipes = service.getRecipes();

        assertNotNull(recipes);
    }
}
