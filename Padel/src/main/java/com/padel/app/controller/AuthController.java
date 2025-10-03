package com.padel.app.controller;

import com.padel.app.dto.auth.AuthResponse;
import com.padel.app.dto.auth.LoginRequest;
import com.padel.app.dto.auth.RegisterRequest;
import com.padel.app.dto.user.UserResponseDTO;
import com.padel.app.model.User;
import com.padel.app.repository.UserRepository;
import com.padel.app.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // === Registro ===
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("El email ya está registrado");
        }

        User user = new User();
        user.setNameUser(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password())); // 🔐 encriptar contraseña
        user.setPhotoUrl(request.photoUrl());
        user.setRole(User.Role.USER);

        User saved = userRepository.save(user);

        return ResponseEntity.ok(new UserResponseDTO(
                saved.getIdUser(),
                saved.getNameUser(),
                saved.getEmail(),
                saved.getPhotoUrl(),
                saved.getRole().name(),
                saved.getPoints()
        ));
    }

    // === Login ===
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // 1. Buscar usuario por email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Validar password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // 3. Generar token JWT usando JwtService
        String token = jwtUtil.generateToken(user);

        // 4. Retornar token y mensaje
        return ResponseEntity.ok(new AuthResponse(
                token,
                "Login exitoso"
        ));
    }
}
