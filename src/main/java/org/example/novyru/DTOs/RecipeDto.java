package org.example.novyru.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RecipeDto {

    private int id;
    private String name;
    private String description;

    private List<IngredientDto> ingredients;
    private List<String> instructions;

    private String difficulty;

    @JsonProperty("meal_type")
    private String mealType;

    private String cuisine;

    @JsonProperty("dietary_tags")
    private List<String> dietaryTags;

    private int servings;

    @JsonProperty("prep_time")
    private int prepTime;

    @JsonProperty("cook_time")
    private int cookTime;

    @JsonProperty("calories_per_serving")
    private int caloriesPerServing;

    private int protein;
}