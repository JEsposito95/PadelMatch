package com.padel.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourtDTO(
        @NotBlank(message = "El nombre de la cancha no puede estar vacío")
        String nameCourt,

        @NotBlank(message = "La dirección no puede estar vacía")
        String direction,

        Double lat,
        Double lng,

        @NotNull(message = "El precio no puede ser nulo")
        @Min(value = 0, message = "El precio debe ser positivo")
        Double price,

        @NotNull(message = "El ID del dueño es obligatorio")
        Long idOwner
) {}
