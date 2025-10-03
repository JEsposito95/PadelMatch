package com.padel.app.dto.booking;

import java.time.LocalDateTime;

public record BookingResponseDTO(
        Long idBooking,
        Long idCourt,
        String nameCourt,
        Long idUser,
        String nameUser,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status
) {}
