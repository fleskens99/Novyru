package org.example.novyru.Application.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class RecipeResponseDto {
    private List<RecipeDto> data;
}