package com.padel.app.controller;

import com.padel.app.dto.statistics.AdminStatsDTO;
import com.padel.app.dto.statistics.TopCourtDTO;
import com.padel.app.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    // === Estadísticas globales para ADMIN ===
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<AdminStatsDTO> getAdminStatistics() {
        return ResponseEntity.ok(statisticsService.getAdminStatistics());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/top-courts")
    public ResponseEntity<List<TopCourtDTO>> getTopCourts() {
        return ResponseEntity.ok(statisticsService.getTopCourts());
    }

}
