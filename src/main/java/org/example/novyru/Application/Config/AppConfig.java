package org.example.novyru.Application.Config;

import org.example.novyru.Application.Services.FavoriteRecipeService;
import org.example.novyru.Application.Services.UserService;
import org.example.novyru.Domain.Interfaces.Repositories.IFavoriteRecipeRepository;
import org.example.novyru.Domain.Interfaces.Repositories.IUserRepository;
import org.example.novyru.Domain.Interfaces.Rules.IFavoriteRecipeRules;
import org.example.novyru.Domain.Interfaces.Rules.IUserRules;
import org.example.novyru.Domain.Rules.FavoriteRecipeRules;
import org.example.novyru.Infrastructure.Repositories.FavoriteRecipeRepository;
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
    @Bean
    public IFavoriteRecipeRepository favoriteRecipeRepository(DataSource dataSource){
        return new FavoriteRecipeRepository(dataSource);
    }

    // Register Application service
    @Bean
    public UserService userService(IUserRepository repository, IUserRules rules) {

        return new UserService(repository,rules);
    }
    @Bean
    public FavoriteRecipeService favoriteRecipeService(IFavoriteRecipeRepository repository, IFavoriteRecipeRules rules){
        return new FavoriteRecipeService(repository, rules);
    }

}
