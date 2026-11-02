
package org.example.novyru.Application.Services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.RequiredArgsConstructor;

import org.example.novyru.Application.DTOs.UserDTOs.LoginUserDto;
import org.example.novyru.Application.DTOs.UserDTOs.RegisterUserDto;
import org.example.novyru.Application.DTOs.UserDTOs.UserResponseDto;
import org.example.novyru.Application.Mappers.UserDtoMapper;

import org.example.novyru.Domain.Models.User;

import org.example.novyru.Domain.Exceptions.User.DuplicateEmailException;
import org.example.novyru.Domain.Exceptions.User.InvalidCredentialsException;
import org.example.novyru.Domain.Exceptions.User.InvalidRegistrationException;

import org.example.novyru.Domain.Interfaces.Repositories.IUserRepository;
import org.example.novyru.Domain.Interfaces.Rules.IUserRules;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class UserService {

    private final IUserRepository userRepository;
    private final IUserRules userRules;
    private final UserDtoMapper userDtoMapper;

    private static final int BCRYPT_COST = 12;

    // REGISTER USER
    public UserResponseDto registerUser(RegisterUserDto dto) {

        try {
            // Validate registration DTO.
            if (dto == null) {
                throw new InvalidRegistrationException(
                        "Registration data is required."
                );
            }

            // Normalize email before validation.
            String email = dto.getEmail() == null
                    ? null
                    : normalizeEmail(dto.getEmail());

            // Validate using Domain rules.
            userRules.validateRegistration(
                    dto.getName(),
                    email,
                    dto.getPassword()
            );

            // Hash the plaintext password.
            String passwordHash = BCrypt.withDefaults()
                    .hashToString(
                            BCRYPT_COST,
                            dto.getPassword().toCharArray()
                    );

            // Application DTO -> Domain Model
            User user = userDtoMapper.toModel(
                    dto,
                    email,
                    passwordHash
            );

            // Save through Domain repository interface.
            boolean saved = userRepository.save(user);

            // Check for duplicate email.
            if (!saved) {
                throw new DuplicateEmailException(
                        "Please use another email address."
                );
            }

            // Domain Model -> Safe response DTO
            return userDtoMapper.toResponse(user);

        } catch (DuplicateEmailException |
                 InvalidRegistrationException e) {

            // Expected Domain exceptions.
            throw e;

        } catch (RuntimeException e) {

            // Unexpected technical failure.
            throw new IllegalStateException(
                    "Registration could not be completed.",
                    e
            );
        }
    }

    // LOGIN USER
    public UserResponseDto loginUser(LoginUserDto dto) {

        try {
            // Validate login input.
            if (dto == null ||
                    dto.getEmail() == null ||
                    dto.getEmail().isBlank() ||
                    dto.getPassword() == null ||
                    dto.getPassword().isEmpty()) {

                throw new InvalidCredentialsException(
                        "Please provide valid credentials."
                );
            }

            // Normalize email.
            String email = normalizeEmail(dto.getEmail());

            // Retrieve Domain User model.
            User user = userRepository
                    .findByEmail(email)
                    .orElseThrow(() ->
                            new InvalidCredentialsException(
                                    "Please try again."
                            )
                    );

            // Verify password with BCrypt.
            boolean validPassword = BCrypt.verifyer()
                    .verify(
                            dto.getPassword().toCharArray(),
                            user.getPasswordHash()
                    ).verified;

            if (!validPassword) {
                throw new InvalidCredentialsException(
                        "Please try again."
                );
            }

            // Domain Model -> Safe response DTO
            return userDtoMapper.toResponse(user);

        } catch (InvalidCredentialsException e) {

            // Expected Domain exception.
            throw e;

        } catch (RuntimeException e) {

            // Unexpected technical failure.
            throw new IllegalStateException(
                    "Login could not be completed.",
                    e
            );
        }
    }

    // NORMALIZE EMAIL
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
