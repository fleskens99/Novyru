
package org.example.novyru.Domain.Interfaces.Repositories;

import org.example.novyru.Domain.Models.User;

import java.util.Optional;

public interface IUserRepository {

    Optional<User> findByEmail(String email);

    boolean save(User user);
}
