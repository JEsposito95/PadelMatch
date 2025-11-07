package com.padel.app.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record BookingDTO(
        @NotNull(message = "El ID de la cancha es obligatorio")
        Long idCourt,

        @NotNull(message = "La fecha de inicio es obligatoria")
        @Future(message = "La hora de inicio debe ser en el futuro")
        LocalDateTime startTime,

        @NotNull(message = "La fecha de fin es obligatoria")
        @Future(message = "La hora de finalización debe ser en el futuro")
        LocalDateTime endTime
) {}
