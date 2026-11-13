
package org.example.novyru.DomainRulesTests;

import org.example.novyru.Domain.Rules.UserRules;
import org.example.novyru.Domain.Exceptions.User.InvalidRegistrationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRuleTests {

    private UserRules userRules;

    private static final String VALID_NAME = "Alex";
    private static final String VALID_EMAIL = "alex@example.com";
    private static final String VALID_PASSWORD = "SecurePassword123";

    @BeforeEach
    void setUp() {
        userRules = new UserRules();
    }

    // ==========================================
    // VALID REGISTRATION TESTS
    // ==========================================

    @Test
    void validateRegistration_WithValidData_ShouldNotThrow() {

        assertDoesNotThrow(() ->
                userRules.validateRegistration(
                        VALID_NAME,
                        VALID_EMAIL,
                        VALID_PASSWORD
                )
        );
    }

    // ==========================================
    // NAME VALIDATION TESTS
    // ==========================================

    @Test
    void validateRegistration_WithNullName_ShouldThrowException() {

        assertThrows(
                InvalidRegistrationException.class,
                () -> userRules.validateRegistration(
                        null,
                        VALID_EMAIL,
                        VALID_PASSWORD
                )
        );
    }

    @Test
    void validateRegistration_WithEmptyName_ShouldThrowException() {

        assertThrows(
                InvalidRegistrationException.class,
                () -> userRules.validateRegistration(
                        "",
                        VALID_EMAIL,
                        VALID_PASSWORD
                )
        );
    }

    @Test
    void validateRegistration_WithOneCharacterName_ShouldThrowException() {

        assertThrows(
                InvalidRegistrationException.class,
                () -> userRules.validateRegistration(
                        "A",
                        VALID_EMAIL,
                        VALID_PASSWORD
                )
        );
    }

    @Test
    void validateRegistration_WithTwoCharacterName_ShouldNotThrow() {

        assertDoesNotThrow(() ->
                userRules.validateRegistration(
                        "Al",
                        VALID_EMAIL,
                        VALID_PASSWORD
                )
        );
    }

    @Test
    void validateRegistration_With80CharacterName_ShouldNotThrow() {

        assertDoesNotThrow(() ->
                userRules.validateRegistration(
                        "A".repeat(80),
                        VALID_EMAIL,
                        VALID_PASSWORD
                )
        );
    }

    @Test
    void validateRegistration_With81CharacterName_ShouldThrowException() {

        assertThrows(
                InvalidRegistrationException.class,
                () -> userRules.validateRegistration(
                        "A".repeat(81),
                        VALID_EMAIL,
                        VALID_PASSWORD
                )
        );
    }

    @Test
    void validateRegistration_WithWhitespaceOnlyName_ShouldThrowException() {

        assertThrows(
                InvalidRegistrationException.class,
                () -> userRules.validateRegistration(
                        "   ",
                        VALID_EMAIL,
                        VALID_PASSWORD
                )
        );
    }

    // ==========================================
    // EMAIL VALIDATION TESTS
    // ==========================================

    @Test
    void validateRegistration_WithNullEmail_ShouldThrowException() {

        assertThrows(
                InvalidRegistrationException.class,
                () -> userRules.validateRegistration(
                        VALID_NAME,
                        null,
                        VALID_PASSWORD
                )
        );
    }

    @Test
    void validateRegistration_WithEmptyEmail_ShouldThrowException() {

        assertThrows(
                InvalidRegistrationException.class,
                () -> userRules.validateRegistration(
                        VALID_NAME,
                        "",
                        VALID_PASSWORD
                )
        );
    }

    @Test
    void validateRegistration_WithEmailWithoutAt_ShouldThrowException() {

        assertThrows(
                InvalidRegistrationException.class,
                () -> userRules.validateRegistration(
                        VALID_NAME,
                        "alexexample.com",
                        VALID_PASSWORD
                )
        );
    }

    @Test
    void validateRegistration_WithEmailWithoutDomainDot_ShouldThrowException() {

        assertThrows(
                InvalidRegistrationException.class,
                () -> userRules.validateRegistration(
                        VALID_NAME,
                        "alex@example",
                        VALID_PASSWORD
                )
        );
    }

    @Test
    void validateRegistration_WithWhitespaceInEmail_ShouldThrowException() {

        assertThrows(
                InvalidRegistrationException.class,
                () -> userRules.validateRegistration(
                        VALID_NAME,
                        "alex @example.com",
                        VALID_PASSWORD
                )
        );
    }

    @Test
    void validateRegistration_WithEmailTooLong_ShouldThrowException() {

        String longEmail =
                "a".repeat(243) + "@example.com";

        assertThrows(
                InvalidRegistrationException.class,
                () -> userRules.validateRegistration(
                        VALID_NAME,
                        longEmail,
                        VALID_PASSWORD
                )
        );
    }

    // ==========================================
    // PASSWORD VALIDATION TESTS
    // ==========================================

    @Test
    void validateRegistration_WithNullPassword_ShouldThrowException() {

        assertThrows(
                InvalidRegistrationException.class,
                () -> userRules.validateRegistration(
                        VALID_NAME,
                        VALID_EMAIL,
                        null
                )
        );
    }

    @Test
    void validateRegistration_WithShortPassword_ShouldThrowException() {

        assertThrows(
                InvalidRegistrationException.class,
                () -> userRules.validateRegistration(
                        VALID_NAME,
                        VALID_EMAIL,
                        "12345678901"
                )
        );
    }

    @Test
    void validateRegistration_With12CharacterPassword_ShouldNotThrow() {

        assertDoesNotThrow(() ->
                userRules.validateRegistration(
                        VALID_NAME,
                        VALID_EMAIL,
                        "123456789012"
                )
        );
    }

    @Test
    void validateRegistration_With72BytePassword_ShouldNotThrow() {

        String password = "a".repeat(72);

        assertDoesNotThrow(() ->
                userRules.validateRegistration(
                        VALID_NAME,
                        VALID_EMAIL,
                        password
                )
        );
    }

    @Test
    void validateRegistration_With73BytePassword_ShouldThrowException() {

        String password = "a".repeat(73);

        assertThrows(
                InvalidRegistrationException.class,
                () -> userRules.validateRegistration(
                        VALID_NAME,
                        VALID_EMAIL,
                        password
                )
        );
    }

    @Test
    void validateRegistration_With72ByteUnicodePassword_ShouldNotThrow() {

        // Each € character is 3 UTF-8 bytes.
        // 24 * 3 = 72 bytes.
        String password = "€".repeat(24);

        assertDoesNotThrow(() ->
                userRules.validateRegistration(
                        VALID_NAME,
                        VALID_EMAIL,
                        password
                )
        );
    }

    @Test
    void validateRegistration_WithUnicodePasswordOver72Bytes_ShouldThrowException() {

        // 25 * 3 = 75 UTF-8 bytes.
        String password = "€".repeat(25);

        assertThrows(
                InvalidRegistrationException.class,
                () -> userRules.validateRegistration(
                        VALID_NAME,
                        VALID_EMAIL,
                        password
                )
        );
    }
}
