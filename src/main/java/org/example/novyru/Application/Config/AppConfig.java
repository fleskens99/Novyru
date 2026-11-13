
package org.example.novyru.Application.Config;

import org.example.novyru.Application.Services.FavoriteRecipeService;
import org.example.novyru.Application.Services.UserService;
import org.example.novyru.Application.Mappers.UserDtoMapper;

import org.example.novyru.Domain.Interfaces.Repositories.IFavoriteRecipeRepository;
import org.example.novyru.Domain.Interfaces.Repositories.IUserRepository;

import org.example.novyru.Domain.Interfaces.Rules.IFavoriteRecipeRules;
import org.example.novyru.Domain.Interfaces.Rules.IUserRules;

import org.example.novyru.Domain.Rules.FavoriteRecipeRules;
import org.example.novyru.Domain.Rules.UserRules;

import org.example.novyru.Infrastructure.Repositories.FavoriteRecipeRepository;
import org.example.novyru.Infrastructure.Repositories.UserRepository;

import org.example.novyru.Infrastructure.Mappers.UserMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    // ===============================
    // INFRASTRUCTURE REPOSITORIES
    // ===============================

    @Bean
    public IUserRepository userRepository(
            DataSource dataSource,
            UserMapper userMapper
    ) {
        return new UserRepository(
                dataSource,
                userMapper
        );
    }

    @Bean
    public IFavoriteRecipeRepository favoriteRecipeRepository(
            DataSource dataSource) {
        return new FavoriteRecipeRepository(
                dataSource
        );
    }

    // ===============================
    // APPLICATION SERVICES
    // ===============================

    @Bean
    public UserService userService(
            IUserRepository userRepository,
            IUserRules userRules,
            UserDtoMapper userDtoMapper
    ) {
        return new UserService(
                userRepository,
                userRules,
                userDtoMapper
        );
    }

    @Bean
    public FavoriteRecipeService favoriteRecipeService(
            IFavoriteRecipeRepository favoriteRecipeRepository,
            IFavoriteRecipeRules favoriteRecipeRules
    ) {
        return new FavoriteRecipeService(
                favoriteRecipeRepository,
                favoriteRecipeRules
        );
    }
}
