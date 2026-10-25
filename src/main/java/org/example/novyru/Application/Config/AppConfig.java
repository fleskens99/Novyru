package org.example.novyru.Application.Config;

import org.example.novyru.Application.Services.UserService;
import org.example.novyru.Domain.Interfaces.IUserRepository;
import org.example.novyru.Infrastructure.Repositories.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    // Register Infrastructure repository
    @Bean
    public IUserRepository userRepository(DataSource dataSource) {
        return new UserRepository(dataSource);
    }

    // Register Application service
    @Bean
    public UserService userService(IUserRepository repository) {
        return new UserService(repository);
    }
}
