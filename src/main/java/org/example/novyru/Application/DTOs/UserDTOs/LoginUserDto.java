package org.example.novyru.Application.DTOs.UserDTOs;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDto {

    private String email;
    private String password;
}
