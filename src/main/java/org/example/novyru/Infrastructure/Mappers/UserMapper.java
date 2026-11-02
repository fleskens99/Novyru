package org.example.novyru.Infrastructure.Mappers;

import org.example.novyru.Domain.Models.User;
import org.example.novyru.Infrastructure.Entities.UserEntity;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserMapper {

    // Database Entity -> Domain Model
    User toModel(UserEntity entity);

    // Domain Model -> Database Entity
    UserEntity toEntity(User model);

}
