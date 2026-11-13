package org.example.novyru.Infrastructure.Mappers;

import org.example.novyru.Domain.Models.Recipe;
import org.example.novyru.Infrastructure.Entities.RecipeEntity;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface RecipeMapper {

    Recipe toModel(RecipeEntity entity);

    RecipeEntity toEntity(Recipe Model);
}
