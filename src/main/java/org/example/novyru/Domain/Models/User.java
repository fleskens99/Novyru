package org.example.novyru.Domain.Models;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class User {

    private final int id;
    private final String name;
    private final String email;
    private final String passwordHash;

}