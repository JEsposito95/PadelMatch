package com.padel.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // registro y login
                        .requestMatchers("/api/courts/availability", "/api/courts").permitAll() // todos pueden ver
                        .requestMatchers("/api/bookings/**").hasAnyRole("USER", "OWNER", "ADMIN") // solo logueados
                        .requestMatchers("/api/users/**").hasAnyRole("OWNER", "ADMIN") // solo owners y admin
                        .anyRequest().authenticated()               // resto requiere login
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                        .accessDeniedHandler(new RestAccessDeniedHandler())
                )
                .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Custom AuthenticationEntryPoint -> 401 JSON
    public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
        private final ObjectMapper mapper = new ObjectMapper();
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
            try {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                Map<String,Object> body = Map.of(
                        "timestamp", java.time.Instant.now().toString(),
                        "status", 401,
                        "message", "No estás autenticado. Por favor inicia sesión."
                );
                mapper.writeValue(response.getOutputStream(), body);
            } catch (Exception e) { }
        }
    }

    // Custom AccessDeniedHandler -> 403 JSON
    public class RestAccessDeniedHandler implements AccessDeniedHandler {
        private final ObjectMapper mapper = new ObjectMapper();
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) {
            try {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                Map<String,Object> body = Map.of(
                        "timestamp", java.time.Instant.now().toString(),
                        "status", 403,
                        "message", "No tienes permiso para realizar esta acción."
                );
                mapper.writeValue(response.getOutputStream(), body);
            } catch (Exception e) { }
        }
    }
}
