package com.padel.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "RegisterRequest", description = "Datos necesarios para registrar un usuario.")
public record RegisterRequest(

        @Schema(example = "Nombre")
        @NotBlank(message = "El nombre no puede estar vacío")
        String name,

        @Schema(example = "email@example.com")
        @Email(message = "Debe ser un email válido")
        @NotBlank(message = "El email no puede estar vacío")
        String email,

        @Schema(example = "password")
        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        String photoUrl
) {}
