
package org.example.novyru.Application.Mappers;

import org.example.novyru.Application.DTOs.IngredientDto;
import org.example.novyru.Domain.Models.Ingredient;

import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface IngredientDtoMapper {

    // DTO -> Domain
    Ingredient toModel(IngredientDto dto);

    // Domain -> DTO
    IngredientDto toDto(Ingredient model);

    // Lists
    List<Ingredient> toModels(List<IngredientDto> dtos);

    List<IngredientDto> toDtos(List<Ingredient> models);
}
