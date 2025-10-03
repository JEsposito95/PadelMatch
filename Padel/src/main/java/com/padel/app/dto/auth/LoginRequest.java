package com.padel.app.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @Email(message = "Debe ser un email válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}
