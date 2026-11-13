
package org.example.novyru.Application.Mappers;

import org.example.novyru.Application.DTOs.UserDTOs.RegisterUserDto;
import org.example.novyru.Application.DTOs.UserDTOs.UserResponseDto;
import org.example.novyru.Domain.Models.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.BeanMapping;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    // Register DTO -> Domain Model
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", expression = "java(dto.getName().trim())")
    @Mapping(target = "email", source = "normalizedEmail")
    @Mapping(target = "passwordHash", source = "passwordHash")
    User toModel(
            RegisterUserDto dto,
            String normalizedEmail,
            String passwordHash
    );

    // Domain Model -> Safe response DTO
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    UserResponseDto toResponse(User user);
}
