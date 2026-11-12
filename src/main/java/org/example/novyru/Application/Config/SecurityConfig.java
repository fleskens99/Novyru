
package org.example.novyru.Application.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {

        http
                // Allow requests from your Vite frontend
                .cors(cors -> cors.configurationSource(
                        corsConfigurationSource()
                ))

                // Development only: allow API requests
                // without CSRF tokens
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/api", "/api/**"
                ))

                .authorizeHttpRequests(auth -> auth

                        // Allow ALL HTTP methods under /api
                        .requestMatchers("/api", "/api/**").permitAll()

                        // Block other paths
                        .anyRequest().denyAll()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // Your local Vite frontend
        config.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        // Allow common HTTP methods
        config.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE"
                )
        );

        // Allow request headers
        config.setAllowedHeaders(List.of("*"));

        // Don't enable cross-origin cookies
        config.setAllowCredentials(false);

        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
