package org.example.novyru.DTOs;

import lombok.Data;

@Data
public class IngredientDto {

    private int id;
    private String name;
    private String category;
    private double quantity;
    private String unit;
    private boolean optional;
}