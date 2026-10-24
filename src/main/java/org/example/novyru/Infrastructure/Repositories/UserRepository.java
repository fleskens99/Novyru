
package org.example.novyru.Infrastructure.Repositories;

import org.example.novyru.Domain.Interfaces.IUserRepository;

import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;
import java.util.Optional;

public class UserRepository implements IUserRepository {

    private final Jdbi jdbi;

    public UserRepository(DataSource dataSource) {
        this.jdbi = Jdbi.create(dataSource);
    }

    // GET PASSWORD HASH
    @Override
    public Optional<String> findPasswordHashByEmail(String email) {

        return jdbi.withHandle(handle ->
                handle.createQuery("""
                        SELECT password_hash
                        FROM users
                        WHERE email = :email
                        """)
                        .bind("email", email)
                        .mapTo(String.class)
                        .findOne()
        );
    }

    // GET USER NAME
    @Override
    public Optional<String> findNameByEmail(String email) {

        return jdbi.withHandle(handle ->
                handle.createQuery("""
                        SELECT name
                        FROM users
                        WHERE email = :email
                        """)
                        .bind("email", email)
                        .mapTo(String.class)
                        .findOne()
        );
    }

    // SAVE USER
    @Override
    public boolean save(
            String name,
            String passwordHash,
            String email
    ) {

        int rows = jdbi.withHandle(handle ->
                handle.createUpdate("""
                        INSERT INTO users
                            (name, email, password_hash)
                        VALUES
                            (:name, :email, :passwordHash)
                        ON CONFLICT (email) DO NOTHING
                        """)
                        .bind("name", name)
                        .bind("email", email)
                        .bind("passwordHash", passwordHash)
                        .execute()
        );

        // True when the user was inserted.
        // False when the email already exists.
        return rows == 1;
    }
}
