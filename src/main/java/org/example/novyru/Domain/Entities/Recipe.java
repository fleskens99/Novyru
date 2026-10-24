package org.example.novyru.Domain.Entities;


import lombok.Data;
import java.util.List;

@Data
public class Recipe {

    private int id;
    private String name;
    private String description;
    private List<Ingredient> ingredients;
    private List<String> instructions;
    private String difficulty;

}
