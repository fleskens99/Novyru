package org.example.novyru.Application.DTOs.UserDTOs;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class UserResponseDto {

    private final String name;
    private final String email;
}
