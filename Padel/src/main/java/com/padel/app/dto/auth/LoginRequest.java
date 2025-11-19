package com.padel.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "Credenciales de acceso.")
public record LoginRequest(

        @Schema(example = "email@example.com")
        @Email(message = "Debe ser un email válido")
        @NotBlank(message = "El email es obligatorio")
        String email,

        @Schema(example = "password")
        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}
