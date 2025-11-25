package com.padel.app.service;

import com.padel.app.dto.auth.AuthResponseDTO;
import com.padel.app.dto.auth.LoginRequestDTO;
import com.padel.app.dto.auth.RegisterRequestDTO;
import com.padel.app.dto.auth.RegisterResponseDTO;
import com.padel.app.exception.GlobalExceptionHandler;
import com.padel.app.model.User;
import com.padel.app.repository.UserRepository;
import com.padel.app.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo electrónico.");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setNameUser(request.name());
        user.setPhotoUrl(request.photoUrl());
        user.setRole(User.Role.USER);
        user.setPoints(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("Usuario registrado: {}", user.getEmail());

        String token = jwtService.generateToken(user);
        return new RegisterResponseDTO(user.getEmail(), token);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException e) {
            throw new GlobalExceptionHandler.UnauthorizedException("Credenciales inválidas.");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new GlobalExceptionHandler.UnauthorizedException("Usuario no encontrado."));

        String token = jwtService.generateToken(user);
        log.info("Usuario autenticado: {} (ID: {})", user.getEmail(), user.getIdUser());
        return new AuthResponseDTO(token);
    }
}
