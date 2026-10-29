package org.example.novyru.ServiceTests;

import at.favre.lib.crypto.bcrypt.BCrypt;

import org.example.novyru.Application.DTOs.UserDTOs.LoginUserDto;
import org.example.novyru.Application.DTOs.UserDTOs.RegisterUserDto;
import org.example.novyru.Application.DTOs.UserDTOs.UserResponseDto;

import org.example.novyru.Application.Services.UserService;
import org.example.novyru.Domain.Exceptions.User.DuplicateEmailException;
import org.example.novyru.Domain.Exceptions.User.InvalidCredentialsException;
import org.example.novyru.Domain.Exceptions.User.InvalidRegistrationException;
import org.example.novyru.Domain.Interfaces.Repositories.IUserRepository;

import org.example.novyru.Domain.Interfaces.Rules.IUserRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private IUserRules userRules;

    private UserService userService;

    private static final String NAME = "Alex";
    private static final String EMAIL = "alex@example.com";
    private static final String PASSWORD = "MySecurePassword123!";

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userRules);
    }

    // -------------------------------
    // REGISTRATION TESTS
    // -------------------------------

    @Test
    void registerUser_WithValidData_ShouldRegisterSuccessfully() {

        // Arrange
        RegisterUserDto dto = new RegisterUserDto(
                NAME,
                EMAIL,
                PASSWORD
        );

        when(userRepository.save(
                anyString(),
                anyString(),
                anyString()
        )).thenReturn(true);

        // Act
        UserResponseDto result = userService.registerUser(dto);

        // Assert
        assertNotNull(result);
        assertEquals(NAME, result.getName());
        assertEquals(EMAIL, result.getEmail());

        verify(userRepository, times(1))
                .save(eq(NAME), anyString(), eq(EMAIL));
    }

    @Test
    void registerUser_ShouldHashPasswordBeforeSaving() {

        // Arrange
        RegisterUserDto dto = new RegisterUserDto(
                NAME,
                EMAIL,
                PASSWORD
        );

        when(userRepository.save(
                anyString(),
                anyString(),
                anyString()
        )).thenReturn(true);

        ArgumentCaptor<String> hashCaptor =
                ArgumentCaptor.forClass(String.class);

        // Act
        userService.registerUser(dto);

        // Assert
        verify(userRepository).save(
                eq(NAME),
                hashCaptor.capture(),
                eq(EMAIL)
        );

        String savedHash = hashCaptor.getValue();

        // Ensure plaintext password is not stored
        assertNotEquals(PASSWORD, savedHash);

        // Ensure BCrypt can verify the original password
        boolean validHash = BCrypt.verifyer()
                .verify(
                        PASSWORD.toCharArray(),
                        savedHash
                ).verified;

        assertTrue(validHash);
    }

    @Test
    void registerUser_ShouldNormalizeEmailAndName() {

        // Arrange
        RegisterUserDto dto = new RegisterUserDto(
                "  Alex  ",
                "  ALEX@EXAMPLE.COM  ",
                PASSWORD
        );

        when(userRepository.save(
                anyString(),
                anyString(),
                anyString()
        )).thenReturn(true);

        // Act
        UserResponseDto result = userService.registerUser(dto);

        // Assert
        assertEquals("Alex", result.getName());
        assertEquals(EMAIL, result.getEmail());

        verify(userRepository).save(
                eq("Alex"),
                anyString(),
                eq(EMAIL)
        );
    }

    @Test
    void registerUser_WithDuplicateEmail_ShouldThrowException() {

        // Arrange
        RegisterUserDto dto = new RegisterUserDto(
                NAME,
                EMAIL,
                PASSWORD
        );

        // Database reports that the email already exists
        when(userRepository.save(
                anyString(),
                anyString(),
                anyString()
        )).thenReturn(false);

        // Act & Assert
        assertThrows(
                DuplicateEmailException.class,
                () -> userService.registerUser(dto)
        );
    }

    @Test
    void registerUser_WithInvalidEmail_ShouldThrowException() {

        RegisterUserDto dto = new RegisterUserDto(
                NAME,
                "invalid-email",
                PASSWORD
        );

        assertThrows(
                InvalidRegistrationException.class,
                () -> userService.registerUser(dto)
        );

        // Invalid input should never reach the database
        verifyNoInteractions(userRepository);
    }

    @Test
    void registerUser_WithShortPassword_ShouldThrowException() {

        RegisterUserDto dto = new RegisterUserDto(
                NAME,
                EMAIL,
                "123"
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
    void registerUser_WhenDatabaseFails_ShouldThrowIllegalStateException() {

        // Arrange
        RegisterUserDto dto = new RegisterUserDto(
                NAME,
                EMAIL,
                PASSWORD
        );

        RuntimeException databaseError =
                new RuntimeException("Database unavailable");

        when(userRepository.save(
                anyString(),
                anyString(),
                anyString()
        )).thenThrow(databaseError);

        // Act
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.registerUser(dto)
        );

        // Assert
        assertEquals(
                "Registration could not be completed.",
                exception.getMessage()
        );

        assertSame(databaseError, exception.getCause());
    }

    // -------------------------------
    // LOGIN TESTS
    // -------------------------------

    @Test
    void loginUser_WithCorrectCredentials_ShouldLoginSuccessfully() {

        // Arrange
        String passwordHash = createPasswordHash();

        LoginUserDto dto = new LoginUserDto(
                EMAIL,
                PASSWORD
        );

        when(userRepository.findPasswordHashByEmail(EMAIL))
                .thenReturn(Optional.of(passwordHash));

        when(userRepository.findNameByEmail(EMAIL))
                .thenReturn(Optional.of(NAME));

        // Act
        UserResponseDto result = userService.loginUser(dto);

        // Assert
        assertNotNull(result);
        assertEquals(NAME, result.getName());
        assertEquals(EMAIL, result.getEmail());

        verify(userRepository).findPasswordHashByEmail(EMAIL);
        verify(userRepository).findNameByEmail(EMAIL);
    }

    @Test
    void loginUser_ShouldNormalizeEmail() {

        // Arrange
        String passwordHash = createPasswordHash();

        LoginUserDto dto = new LoginUserDto(
                "  ALEX@EXAMPLE.COM  ",
                PASSWORD
        );

        when(userRepository.findPasswordHashByEmail(EMAIL))
                .thenReturn(Optional.of(passwordHash));

        when(userRepository.findNameByEmail(EMAIL))
                .thenReturn(Optional.of(NAME));

        // Act
        UserResponseDto result = userService.loginUser(dto);

        // Assert
        assertEquals(EMAIL, result.getEmail());

        verify(userRepository).findPasswordHashByEmail(EMAIL);
    }

    @Test
    void loginUser_WithWrongPassword_ShouldThrowException() {

        // Arrange
        String passwordHash = createPasswordHash();

        LoginUserDto dto = new LoginUserDto(
                EMAIL,
                "WrongPassword123!"
        );

        when(userRepository.findPasswordHashByEmail(EMAIL))
                .thenReturn(Optional.of(passwordHash));

        // Act & Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(dto)
        );

        // Name should not be requested after wrong password
        verify(userRepository, never()).findNameByEmail(anyString());
    }

    @Test
    void loginUser_WithUnknownEmail_ShouldThrowException() {

        // Arrange
        LoginUserDto dto = new LoginUserDto(
                "unknown@example.com",
                PASSWORD
        );

        when(userRepository.findPasswordHashByEmail(
                "unknown@example.com"
        )).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(dto)
        );

        verify(userRepository, never()).findNameByEmail(anyString());
    }

    @Test
    void loginUser_WithNullDto_ShouldThrowException() {

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(null)
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void loginUser_WhenNameIsMissing_ShouldThrowException() {

        // Arrange
        LoginUserDto dto = new LoginUserDto(EMAIL, PASSWORD);

        when(userRepository.findPasswordHashByEmail(EMAIL))
                .thenReturn(Optional.of(createPasswordHash()));

        when(userRepository.findNameByEmail(EMAIL))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(dto)
        );
    }

    @Test
    void loginUser_WhenDatabaseFails_ShouldThrowIllegalStateException() {

        // Arrange
        LoginUserDto dto = new LoginUserDto(EMAIL, PASSWORD);

        RuntimeException databaseError =
                new RuntimeException("Database unavailable");

        when(userRepository.findPasswordHashByEmail(EMAIL))
                .thenThrow(databaseError);

        // Act
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.loginUser(dto)
        );

        // Assert
        assertEquals(
                "Login could not be completed.",
                exception.getMessage()
        );

        assertSame(databaseError, exception.getCause());
    }

    // -------------------------------
    // TEST HELPER
    // -------------------------------

    private String createPasswordHash() {
        return BCrypt.withDefaults()
                .hashToString(12, PASSWORD.toCharArray());
    }
}
