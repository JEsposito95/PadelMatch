package com.padel.app.dto;

import java.time.LocalDateTime;

public record BookingResponseDTO(
        Long id,
        Long courtId,
        String courtName,
        Long userId,
        String userName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status
) {}
