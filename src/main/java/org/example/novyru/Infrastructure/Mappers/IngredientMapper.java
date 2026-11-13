package org.example.novyru.Infrastructure.Mappers;

import org.example.novyru.Domain.Models.Ingredient;
import org.example.novyru.Infrastructure.Entities.IngredientEntity;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface IngredientMapper {

    Ingredient toModel(IngredientEntity entity);

    IngredientEntity toEntity(Ingredient Model);

    List<Ingredient> toModels(List<IngredientEntity> entities);

    List<IngredientEntity> toEntities(List<Ingredient> models);
}
