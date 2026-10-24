package org.example.novyru.Application.DTOs.UserDTOs;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class RegisterUserDto {

    private String name;
    private String email;
    private String password;
}
