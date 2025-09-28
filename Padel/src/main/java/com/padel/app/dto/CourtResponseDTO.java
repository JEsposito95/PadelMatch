package com.padel.app.dto;

public record CourtResponseDTO(
        Long id,
        String nombre,
        String direccion,
        Double lat,
        Double lng,
        Double price,
        Long ownerId,
        String ownerName
) {}
