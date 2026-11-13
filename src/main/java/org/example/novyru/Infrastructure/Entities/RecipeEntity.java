package org.example.novyru.Infrastructure.Entities;


import lombok.Data;
import java.util.List;

@Data
public class RecipeEntity {

    private int id;
    private String name;
    private String description;
    private List<IngredientEntity> ingredients;
    private List<String> instructions;
    private String difficulty;

}
