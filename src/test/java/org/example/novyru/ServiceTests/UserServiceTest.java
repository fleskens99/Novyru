
package org.example.novyru.ServiceTests;

import at.favre.lib.crypto.bcrypt.BCrypt;

import org.example.novyru.Application.DTOs.UserDTOs.LoginUserDto;
import org.example.novyru.Application.DTOs.UserDTOs.RegisterUserDto;
import org.example.novyru.Application.Mappers.UserDtoMapper;
import org.example.novyru.Application.Services.UserService;

import org.example.novyru.Domain.Models.User;
import org.example.novyru.Domain.Exceptions.User.DuplicateEmailException;
import org.example.novyru.Domain.Exceptions.User.InvalidCredentialsException;
import org.example.novyru.Domain.Exceptions.User.InvalidRegistrationException;
import org.example.novyru.Domain.Interfaces.Repositories.IUserRepository;
import org.example.novyru.Domain.Interfaces.Rules.IUserRules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IUserRules userRules;

    @Mock
    private UserDtoMapper userDtoMapper;

    private UserService userService;

    private static final String NAME = "Alex";
    private static final String EMAIL = "alex@example.com";
    private static final String PASSWORD = "MySecurePassword123!";

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepository,
                userRules,
                userDtoMapper
        );
    }

    // ==========================================
    // TEST HELPERS
    // ==========================================

    private RegisterUserDto validRegistration() {
        return new RegisterUserDto(
                NAME,
                EMAIL,
                PASSWORD
        );
    }

    private String createPasswordHash() {
        return BCrypt.withDefaults()
                .hashToString(
                        12,
                        PASSWORD.toCharArray()
                );
    }

    private User createMockUser() {
        User user = mock(User.class);

        when(user.getName()).thenReturn(NAME);
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getPasswordHash())
                .thenReturn(createPasswordHash());

        return user;
    }

    // ==========================================
    // REGISTRATION TESTS
    // ==========================================

    @Test
    void registerUser_WithValidData_ShouldSaveUser() {

        when(userRepository.save(any(User.class)))
                .thenReturn(true);

        userService.registerUser(validRegistration());

        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    void registerUser_ShouldCallValidationRules() {

        when(userRepository.save(any(User.class)))
                .thenReturn(true);

        userService.registerUser(validRegistration());

        verify(userRules).validateRegistration(
                eq(NAME),
                eq(EMAIL),
                eq(PASSWORD)
        );
    }

    @Test
    void registerUser_WithDuplicateEmail_ShouldThrowException() {

        when(userRepository.save(any(User.class)))
                .thenReturn(false);

        assertThrows(
                DuplicateEmailException.class,
                () -> userService.registerUser(
                        validRegistration()
                )
        );
    }

    @Test
    void registerUser_WithInvalidEmail_ShouldThrowException() {

        RegisterUserDto dto = new RegisterUserDto(
                NAME,
                "invalid-email",
                PASSWORD
        );

        doThrow(new InvalidRegistrationException(
                "Invalid registration data"
        )).when(userRules).validateRegistration(
                anyString(),
                anyString(),
                anyString()
        );

        assertThrows(
                InvalidRegistrationException.class,
                () -> userService.registerUser(dto)
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void registerUser_WithShortPassword_ShouldThrowException() {

        RegisterUserDto dto = new RegisterUserDto(
                NAME,
                EMAIL,
                "123"
        );

        doThrow(new InvalidRegistrationException(
                "Password is too short"
        )).when(userRules).validateRegistration(
                anyString(),
                anyString(),
                anyString()
        );

        assertThrows(
                InvalidRegistrationException.class,
                () -> userService.registerUser(dto)
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void registerUser_WithNullDto_ShouldThrowException() {

        assertThrows(
                InvalidRegistrationException.class,
                () -> userService.registerUser(null)
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void registerUser_WhenDatabaseFails_ShouldThrowException() {

        RuntimeException databaseError =
                new RuntimeException("Database unavailable");

        when(userRepository.save(any(User.class)))
                .thenThrow(databaseError);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.registerUser(
                        validRegistration()
                )
        );

        assertEquals(
                "Registration could not be completed.",
                exception.getMessage()
        );

        assertSame(
                databaseError,
                exception.getCause()
        );
    }

    // ==========================================
    // LOGIN TESTS
    // ==========================================

    @Test
    void loginUser_WithNullDto_ShouldThrowException() {

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(null)
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void loginUser_WithEmptyEmail_ShouldThrowException() {

        LoginUserDto dto = new LoginUserDto(
                "",
                PASSWORD
        );

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(dto)
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void loginUser_WithEmptyPassword_ShouldThrowException() {

        LoginUserDto dto = new LoginUserDto(
                EMAIL,
                ""
        );

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(dto)
        );

        verifyNoInteractions(userRepository);
    }
}
