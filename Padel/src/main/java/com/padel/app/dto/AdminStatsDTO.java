package com.padel.app.dto;

import java.math.BigDecimal;

public record AdminStatsDTO(
        long totalUsuarios,
        long totalCanchas,
        long totalReservas,
        double promedioReservasPorUsuario,
        BigDecimal ingresosTotales
) {}