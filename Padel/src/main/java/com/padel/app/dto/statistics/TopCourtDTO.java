package com.padel.app.dto.statistics;

public record TopCourtDTO(
        Long idCourt,
        String nameCourt,
        Long totalBookings
) {
}
