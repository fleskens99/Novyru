package org.example.novyru.Controllers;

import org.example.novyru.DTOs.RecipeDto;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeController {
    @PostMapping("/recipe")
    public String addRecipe(@RequestBody RecipeDto recipeDto){
        return "Created";
    }

    @PutMapping("/recipe")
    public String updatedRecipe(@RequestBody RecipeDto recipeDto){
        return "Updated";
    }
}
