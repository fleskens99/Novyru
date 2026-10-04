package org.example.novyru.Controllers;

import org.example.novyru.DTOs.RecipeDto;
import org.example.novyru.Services.RecipeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService){
        this.recipeService = recipeService;

    }

    @GetMapping("/recipes")
    public List<RecipeDto> getRecipes() {
        return recipeService.getRecipes();
    }
}
