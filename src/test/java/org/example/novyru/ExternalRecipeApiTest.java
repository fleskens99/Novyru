
package org.example.novyru;

import org.example.novyru.Application.Services.ExternalRecipeService;
import org.example.novyru.Application.DTOs.RecipeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExternalRecipeApiTest {

    @Autowired
    private ExternalRecipeService externalRecipeApi;

    @Test
    void testGetRecipes() {

        // Call the real external API
        List<RecipeDto> recipes = externalRecipeApi.getRecipes();

        // Check that we received a response
        assertNotNull(recipes);

        // Print results
        System.out.println("Recipes found: " + recipes.size());

        for (RecipeDto recipe : recipes) {
            System.out.println(recipe);
        }
    }
}
