package com.padel.app.dto.statistics;

import java.math.BigDecimal;

public record AdminStatsDTO(
        long totalUsers,
        long totalCourts,
        long totalBookings,
        double avgBookingsPerUser,
        BigDecimal totalIncome
) {}