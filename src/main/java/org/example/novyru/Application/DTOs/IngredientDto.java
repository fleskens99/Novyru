package org.example.novyru.Application.DTOs;

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