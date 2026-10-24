package org.example.novyru.Domain.Entities;

import lombok.Getter;

@Getter
public class User {

    private final String name;
    private final String email;
    private final String passwordHash;

    public User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
