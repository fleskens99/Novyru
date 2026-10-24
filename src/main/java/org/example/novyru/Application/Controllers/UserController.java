package org.example.novyru.Application.Controllers;

import lombok.RequiredArgsConstructor;

import org.example.novyru.Application.Services.UserService;
import org.example.novyru.Application.DTOs.UserDTOs.RegisterUserDto;
import org.example.novyru.Application.DTOs.UserDTOs.LoginUserDto;
import org.example.novyru.Application.DTOs.UserDTOs.UserResponseDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // POST /api/users
    // Creates a new user account
    @PostMapping
    public ResponseEntity<UserResponseDto> registerUser(
            @RequestBody RegisterUserDto dto
    ) {

        UserResponseDto user = userService.registerUser(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    // POST /api/users/login
    // Verifies a user's login credentials
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> loginUser(
            @RequestBody LoginUserDto dto
    ) {

        UserResponseDto user = userService.loginUser(dto);

        return ResponseEntity.ok(user);
    }
}
