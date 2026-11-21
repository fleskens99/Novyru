package org.example.novyru.Application.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AddFavoriteRequestDto(
        int recipeId
) {
}
