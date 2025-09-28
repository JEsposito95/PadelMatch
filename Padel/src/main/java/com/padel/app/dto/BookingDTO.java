package com.padel.app.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record BookingDTO(
        @NotNull(message = "El ID de la cancha es obligatorio")
        Long courtId,

        @NotNull(message = "El ID del usuario es obligatorio")
        Long userId,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDateTime startTime,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDateTime endTime
) {}
