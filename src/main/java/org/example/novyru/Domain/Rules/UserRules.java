
package org.example.novyru.Domain.Rules;

import org.example.novyru.Domain.Exceptions.User.InvalidRegistrationException;
import org.example.novyru.Domain.Interfaces.Rules.IUserRules;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Component
public final class UserRules implements IUserRules {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Override
    public void validateRegistration(
            String name,
            String email,
            String password
    ) {

        // Validate name.
        if (name == null ||
                name.trim().length() < 2 ||
                name.trim().length() > 80) {

            throw new InvalidRegistrationException(
                    "Name must contain 2 to 80 characters."
            );
        }

        // Validate email.
        if (email == null ||
                email.length() > 254 ||
                !EMAIL_PATTERN.matcher(email).matches()) {

            throw new InvalidRegistrationException(
                    "Invalid email address."
            );
        }

        // Validate password length.
        if (password == null || password.length() < 12) {

            throw new InvalidRegistrationException(
                    "Password must contain at least 12 characters."
            );
        }

        // Validate BCrypt password byte limit.
        int passwordBytes = password
                .getBytes(StandardCharsets.UTF_8).length;

        if (passwordBytes > 72) {

            throw new InvalidRegistrationException(
                    "Password cannot exceed 72 UTF-8 bytes."
            );
        }
    }
}
