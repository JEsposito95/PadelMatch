package com.padel.app.dto;

public record UserResponseDTO(
        Long id,
        String nombre,
        String email,
        String fotoURL,
        String role,
        Integer puntos
) {
}
