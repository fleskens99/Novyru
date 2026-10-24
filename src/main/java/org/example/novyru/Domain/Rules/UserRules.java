
package org.example.novyru.Domain.Rules;

import org.example.novyru.Domain.Exceptions.InvalidRegistrationException;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public final class UserRules {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private UserRules() {
        // Prevent creating instances of this utility class.
    }

    public static void validateRegistration(
            String name,
            String email,
            String password
    ) {

        // Name must be between 2 and 80 characters.
        if (name == null ||
                name.trim().length() < 2 ||
                name.trim().length() > 80) {

            throw new InvalidRegistrationException(
                    "Name must contain 2 to 80 characters."
            );
        }

        // Email must have a valid basic format.
        if (email == null ||
                email.length() > 254 ||
                !EMAIL_PATTERN.matcher(email).matches()) {

            throw new InvalidRegistrationException(
                    "Invalid email address."
            );
        }

        // Password must meet the minimum length.
        if (password == null || password.length() < 12) {

            throw new InvalidRegistrationException(
                    "Password must contain at least 12 characters."
            );
        }

        // BCrypt only accepts up to 72 password bytes.
        int passwordBytes = password
                .getBytes(StandardCharsets.UTF_8).length;

        if (passwordBytes > 72) {
            throw new InvalidRegistrationException(
                    "Password cannot exceed 72 UTF-8 bytes."
            );
        }
    }
}
