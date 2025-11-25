package com.padel.app.dto.auth;

public record RegisterResponseDTO(
        String email,
        String token
) {
}
