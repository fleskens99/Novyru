
package org.example.novyru.Domain.Interfaces.Repositories;

import java.util.Optional;

public interface IUserRepository {

    Optional<String> findPasswordHashByEmail(String email);

    Optional<String> findNameByEmail(String email);

    boolean save(
            String name,
            String passwordHash,
            String email
    );
}
