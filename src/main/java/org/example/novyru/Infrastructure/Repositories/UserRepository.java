
package org.example.novyru.Infrastructure.Repositories;

import org.example.novyru.Domain.Interfaces.Repositories.IUserRepository;
import org.example.novyru.Domain.Models.User;
import org.example.novyru.Infrastructure.Entities.UserEntity;
import org.example.novyru.Infrastructure.Mappers.UserMapper;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository implements IUserRepository {

    private final Jdbi jdbi;
    private final UserMapper userMapper;

    public UserRepository(
            DataSource dataSource,
            UserMapper userMapper
    ) {
        this.jdbi = Jdbi.create(dataSource);
        this.userMapper = userMapper;
    }

    // FIND USER BY EMAIL
    @Override
    public Optional<User> findByEmail(String email) {

        return jdbi.withHandle(handle ->
                handle.createQuery("""
                        SELECT id, name, email, password_hash
                        FROM users
                        WHERE email = :email
                        """)
                        .bind("email", email)

                        // SQL row -> Infrastructure Entity
                        .map((rs, ctx) -> new UserEntity(
                                rs.getLong("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("password_hash")
                        ))

                        .findOne()

                        // Entity -> Domain Model
                        .map(userMapper::toModel)
        );
    }

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
                    ON DUPLICATE KEY UPDATE
                        email = email
                    """)
                        .bind("name", name)
                        .bind("email", email)
                        .bind("passwordHash", passwordHash)
                        .execute()
        );

        return rows == 1;
    }
}
