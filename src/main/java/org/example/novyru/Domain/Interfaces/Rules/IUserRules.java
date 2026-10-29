
package org.example.novyru.Domain.Interfaces.Rules;

public interface IUserRules {

    void validateRegistration(
            String name,
            String email,
            String password
    );
}
