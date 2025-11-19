package com.padel.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AuthResponse", description = "Token generado luego del login.")
public record AuthResponse(
        @Schema(description = "Token JWT válido", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token
) {}
