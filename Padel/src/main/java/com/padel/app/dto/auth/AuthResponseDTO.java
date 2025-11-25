package com.padel.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AuthResponseDTO", description = "Token generado luego del login.")
public record AuthResponseDTO(
        @Schema(description = "Token JWT válido", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token
) {}
