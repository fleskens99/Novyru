package org.example.novyru.Domain.Entities;

import lombok.Data;

@Data
public class Ingredient {
    private int id;
    private String name;
    private String category;
    private double quantity;
    private String unit;
}
