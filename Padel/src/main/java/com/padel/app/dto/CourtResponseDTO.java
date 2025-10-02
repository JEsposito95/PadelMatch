package com.padel.app.dto;

public record CourtResponseDTO(
        Long idCourt,
        String nameCourt,
        String direction,
        Double lat,
        Double lng,
        Double price,
        Long idOwner,
        String nameOwner
) {}
