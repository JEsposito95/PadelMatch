package com.padel.app.dto.user;

public record UserResponseDTO(
        Long idUser,
        String nameUser,
        String email,
        String photoUrl,
        String role,
        Integer points
) {
}
