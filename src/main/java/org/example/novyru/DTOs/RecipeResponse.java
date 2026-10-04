package org.example.novyru.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class RecipeResponse {
    private List<RecipeDto> data;
}