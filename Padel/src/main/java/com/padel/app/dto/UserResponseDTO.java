package com.padel.app.dto;

public record UserResponseDTO(
        Long idUser,
        String nameUser,
        String email,
        String photoUrl,
        String role,
        Integer points
) {
}
