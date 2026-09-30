package org.example.novyru.DTOs;

import lombok.Data;

@Data
public class RecipeDto {

    private int id;
    private String name;
    private String description;
    private String ingredients;
    private String instructions;
    private int preparationTimeMinutes;

}