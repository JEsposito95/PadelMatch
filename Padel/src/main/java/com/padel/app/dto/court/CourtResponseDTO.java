package com.padel.app.dto.court;

public record CourtResponseDTO(
        Long idCourt,
        String nameCourt,
        String direction,
        Double lat,
        Double lng,
        java.math.BigDecimal price,
        Long idOwner,
        String nameOwner
) {}
