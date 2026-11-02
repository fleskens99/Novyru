package org.example.novyru.Infrastructure.Entities;

import lombok.Data;

@Data
public class IngredientEntity {
    private int id;
    private String name;
    private String category;
    private double quantity;
    private String unit;
}
