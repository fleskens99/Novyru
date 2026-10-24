
package org.example.novyru.Application.Services;

import at.favre.lib.crypto.bcrypt.BCrypt;

import lombok.RequiredArgsConstructor;

import org.example.novyru.Application.DTOs.UserDTOs.LoginUserDto;
import org.example.novyru.Application.DTOs.UserDTOs.RegisterUserDto;
import org.example.novyru.Application.DTOs.UserDTOs.UserResponseDto;

import org.example.novyru.Domain.Exceptions.DuplicateEmailException;
import org.example.novyru.Domain.Exceptions.InvalidCredentialsException;
import org.example.novyru.Domain.Exceptions.InvalidRegistrationException;

import org.example.novyru.Domain.Interfaces.IUserRepository;
import org.example.novyru.Domain.Rules.UserRules;

import java.util.Locale;

@RequiredArgsConstructor
public class UserService {

    private final IUserRepository userRepository;

    private static final int BCRYPT_COST = 12;

    // REGISTER USER
    public UserResponseDto registerUser(RegisterUserDto dto) {

        try {

            if (dto == null) {
                throw new InvalidRegistrationException(
                        "Registration data is required."
                );
            }

            // Normalize email before validation.
            String email = dto.getEmail() == null
                    ? null
                    : normalizeEmail(dto.getEmail());

            // Validate registration using Domain rules.
            UserRules.validateRegistration(
                    dto.getName(),
                    email,
                    dto.getPassword()
            );

            String name = dto.getName().trim();

            // Hash the plaintext password.
            String passwordHash = BCrypt.withDefaults()
                    .hashToString(
                            BCRYPT_COST,
                            dto.getPassword().toCharArray()
                    );

            // Save through the Domain interface.
            boolean saved = userRepository.save(
                    name,
                    passwordHash,
                    email
            );

            // Domain business rule: no duplicate email.
            if (!saved) {
                throw new DuplicateEmailException();
            }

            // Never return the password hash.
            return new UserResponseDto(name, email);

        } catch (DuplicateEmailException |
                 InvalidRegistrationException e) {

            // Expected business failures.
            throw e;

        } catch (RuntimeException e) {

            // Unexpected errors, including database failures.
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

                throw new InvalidCredentialsException();
            }

            String email = normalizeEmail(dto.getEmail());

            // Retrieve the stored password hash.
            String passwordHash = userRepository
                    .findPasswordHashByEmail(email)
                    .orElseThrow(InvalidCredentialsException::new);

            // Verify the entered password.
            boolean validPassword = BCrypt.verifyer()
                    .verify(
                            dto.getPassword().toCharArray(),
                            passwordHash
                    ).verified;

            if (!validPassword) {
                throw new InvalidCredentialsException();
            }

            // Retrieve the user's name.
            String name = userRepository
                    .findNameByEmail(email)
                    .orElseThrow(InvalidCredentialsException::new);

            // Return safe user information.
            return new UserResponseDto(name, email);

        } catch (InvalidCredentialsException e) {

            // Expected authentication failure.
            throw e;

        } catch (RuntimeException e) {

            // Unexpected errors, including database failures.
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
