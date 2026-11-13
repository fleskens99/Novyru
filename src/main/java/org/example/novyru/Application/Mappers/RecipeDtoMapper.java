
package org.example.novyru.Application.Mappers;

import org.example.novyru.Application.DTOs.RecipeDto;
import org.example.novyru.Domain.Models.Recipe;

import org.mapstruct.Mapper;
import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {IngredientDtoMapper.class}
)
public interface RecipeDtoMapper {

    // DTO -> Domain Model
    Recipe toModel(RecipeDto dto);

    // Domain Model -> DTO
    RecipeDto toDto(Recipe model);

    // List conversions
    List<Recipe> toModels(List<RecipeDto> dtos);

    List<RecipeDto> toDtos(List<Recipe> models);
}
