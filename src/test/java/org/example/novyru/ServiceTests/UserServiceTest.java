package org.example.novyru.ServiceTests;

import at.favre.lib.crypto.bcrypt.BCrypt;

import org.example.novyru.Application.DTOs.UserDTOs.LoginUserDto;
import org.example.novyru.Application.DTOs.UserDTOs.RegisterUserDto;
import org.example.novyru.Application.DTOs.UserDTOs.UserResponseDto;
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

import org.mapstruct.factory.Mappers;

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

    private UserDtoMapper userDtoMapper;
    private UserService userService;

    private static final String NAME = "Alex";
    private static final String EMAIL = "alex@example.com";
    private static final String PASSWORD = "MySecurePassword123!";

    @BeforeEach
    void setUp() {
        // Use the actual generated MapStruct mapper.
        userDtoMapper = Mappers.getMapper(UserDtoMapper.class);

        userService = new UserService(
                userRepository,
                userRules,
                userDtoMapper
        );
    }

    // ==========================================
    // HELPERS
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
                .hashToString(12, PASSWORD.toCharArray());
    }

    private User createLoginUser() {
        User user = mock(User.class);

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

        UserResponseDto result =
                userService.registerUser(validRegistration());

        assertNotNull(result);
        assertEquals(NAME, result.getName());
        assertEquals(EMAIL, result.getEmail());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_ShouldCallValidationRules() {

        when(userRepository.save(any(User.class)))
                .thenReturn(true);

        userService.registerUser(validRegistration());

        verify(userRules).validateRegistration(
                NAME,
                EMAIL,
                PASSWORD
        );
    }

    @Test
    void registerUser_ShouldHashPasswordBeforeSaving() {

        when(userRepository.save(any(User.class)))
                .thenReturn(true);

        userService.registerUser(validRegistration());

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        String savedHash =
                captor.getValue().getPasswordHash();

        assertNotEquals(PASSWORD, savedHash);

        assertTrue(
                BCrypt.verifyer()
                        .verify(
                                PASSWORD.toCharArray(),
                                savedHash
                        ).verified
        );
    }

    @Test
    void registerUser_ShouldNormalizeNameAndEmail() {

        when(userRepository.save(any(User.class)))
                .thenReturn(true);

        RegisterUserDto dto = new RegisterUserDto(
                "  Alex  ",
                "  ALEX@EXAMPLE.COM  ",
                PASSWORD
        );

        UserResponseDto result =
                userService.registerUser(dto);

        assertEquals(NAME, result.getName());
        assertEquals(EMAIL, result.getEmail());

        verify(userRules).validateRegistration(
                "  Alex  ",
                EMAIL,
                PASSWORD
        );
    }

    @Test
    void registerUser_WithDuplicateEmail_ShouldThrowException() {

        when(userRepository.save(any(User.class)))
                .thenReturn(false);

        assertThrows(
                DuplicateEmailException.class,
                () -> userService.registerUser(validRegistration())
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
                NAME,
                "invalid-email",
                PASSWORD
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
    void registerUser_WhenDatabaseFails_ShouldThrowException() {

        RuntimeException databaseError =
                new RuntimeException("Database unavailable");

        when(userRepository.save(any(User.class)))
                .thenThrow(databaseError);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.registerUser(validRegistration())
        );

        assertEquals(
                "Registration could not be completed.",
                exception.getMessage()
        );

        assertSame(databaseError, exception.getCause());
    }

    // ==========================================
    // LOGIN TESTS
    // ==========================================

    @Test
    void loginUser_WithValidCredentials_ShouldLogin() {

        User user = createLoginUser();

        when(user.getName()).thenReturn(NAME);
        when(user.getEmail()).thenReturn(EMAIL);

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        UserResponseDto result = userService.loginUser(
                new LoginUserDto(EMAIL, PASSWORD)
        );

        assertNotNull(result);
        assertEquals(NAME, result.getName());
        assertEquals(EMAIL, result.getEmail());
    }

    @Test
    void loginUser_ShouldNormalizeEmail() {

        User user = createLoginUser();

        when(user.getName()).thenReturn(NAME);
        when(user.getEmail()).thenReturn(EMAIL);

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        UserResponseDto result = userService.loginUser(
                new LoginUserDto(
                        "  ALEX@EXAMPLE.COM  ",
                        PASSWORD
                )
        );

        assertEquals(EMAIL, result.getEmail());

        verify(userRepository).findByEmail(EMAIL);
    }

    @Test
    void loginUser_WithWrongPassword_ShouldThrowException() {

        User user = createLoginUser();

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(
                        new LoginUserDto(
                                EMAIL,
                                "WrongPassword123!"
                        )
                )
        );
    }

    @Test
    void loginUser_WithUnknownEmail_ShouldThrowException() {

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(
                        new LoginUserDto(
                                "unknown@example.com",
                                PASSWORD
                        )
                )
        );
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
    void loginUser_WithEmptyEmail_ShouldThrowException() {

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(
                        new LoginUserDto("", PASSWORD)
                )
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void loginUser_WithEmptyPassword_ShouldThrowException() {

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(
                        new LoginUserDto(EMAIL, "")
                )
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void loginUser_WhenDatabaseFails_ShouldThrowException() {

        RuntimeException databaseError =
                new RuntimeException("Database unavailable");

        when(userRepository.findByEmail(EMAIL))
                .thenThrow(databaseError);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.loginUser(
                        new LoginUserDto(EMAIL, PASSWORD)
                )
        );

        assertEquals(
                "Login could not be completed.",
                exception.getMessage()
        );

        assertSame(databaseError, exception.getCause());
    }
}
