
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

    // SAVE USER
    @Override
    public boolean save(User user) {

        // Domain Model -> Infrastructure Entity
        UserEntity entity = userMapper.toEntity(user);

        try {
            int rows = jdbi.withHandle(handle ->
                    handle.createUpdate("""
                            INSERT INTO users
                                (name, email, password_hash)
                            VALUES
                                (:name, :email, :passwordHash)
                            """)
                            .bind("name", entity.getName())
                            .bind("email", entity.getEmail())
                            .bind("passwordHash", entity.getPasswordHash())
                            .execute()
            );

            return rows == 1;

        } catch (UnableToExecuteStatementException e) {

            // MySQL error 1062 = Duplicate key.
            for (Throwable cause = e;
                 cause != null;
                 cause = cause.getCause()) {

                if (cause instanceof SQLException sqlException
                        && sqlException.getErrorCode() == 1062) {
                    return false;
                }
            }

            // Other database errors must not be ignored.
            throw e;
        }
    }
}
