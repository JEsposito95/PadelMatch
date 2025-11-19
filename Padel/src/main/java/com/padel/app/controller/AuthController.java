package com.padel.app.controller;

import com.padel.app.dto.auth.AuthResponse;
import com.padel.app.dto.auth.LoginRequest;
import com.padel.app.dto.auth.RegisterRequest;
import com.padel.app.dto.user.UserResponseDTO;
import com.padel.app.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticación", description = "Endpoints para registro y login de usuarios.")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // === Registro ===
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Crea un usuario nuevo. Devuelve el usuario creado sin la contraseña.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Usuario registrado exitosamente",
                            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos inválidos o email ya registrado"
                    )
            }
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // === Login ===
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario y devuelve un token JWT.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login correcto",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciales inválidas"
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
