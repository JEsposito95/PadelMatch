package com.padel.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desactiva CSRF para pruebas con Postman
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permite todas las peticiones sin autenticación
                );
        return http.build();
    }
}
