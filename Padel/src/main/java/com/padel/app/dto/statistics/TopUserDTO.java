package com.padel.app.dto.statistics;

public record TopUserDTO(
        Long idUser,
        String username,
        String email,
        Long totalBookings
) {}