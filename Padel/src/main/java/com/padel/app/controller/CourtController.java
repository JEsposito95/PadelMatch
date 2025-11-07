package com.padel.app.controller;

import com.padel.app.dto.court.CourtDTO;
import com.padel.app.dto.court.CourtResponseDTO;
import com.padel.app.service.CourtService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courts")
public class CourtController {

    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    // === Ver todas las canchas (público) ===
    @GetMapping
    @PermitAll
    public ResponseEntity<List<CourtResponseDTO>> getAllCourts() {
        return ResponseEntity.ok(courtService.getAllCourts());
    }

    // === Ver cancha por id (público) ===
    @GetMapping("/{id}")
    @PermitAll
    public ResponseEntity<CourtResponseDTO> getCourtById(@PathVariable Long id) {
        return ResponseEntity.ok(courtService.getCourtById(id));
    }

    // === Ver disponibilidad de cancha (público) ===
    @GetMapping("/availability")
    @PermitAll
    public ResponseEntity<List<CourtResponseDTO>> getAvailableCourts(
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        List<CourtResponseDTO> available = courtService.getAvailableCourts(startTime, endTime);
        return ResponseEntity.ok(available);
    }

    // === Crear cancha (solo OWNER o ADMIN) ===
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<CourtResponseDTO> createCourt(@Valid @RequestBody CourtDTO courtDTO, Authentication auth) {
        CourtResponseDTO created = courtService.createCourt(courtDTO, auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // === Eliminar cancha ===
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<Map<String, String>> deleteCourt(@PathVariable Long id, Authentication auth) {
        courtService.deleteCourtIfAllowed(id, auth);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cancha eliminada correctamente.");
        return ResponseEntity.ok(response);
    }

    // === Actualizar cancha completa ===
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<CourtResponseDTO> updateCourt(
            @PathVariable Long id,
            @Valid @RequestBody CourtDTO dto,
            Authentication auth
    ) {
        return ResponseEntity.ok(courtService.updateCourtIfAllowed(id, dto, auth));
    }

    // === Actualización cancha parcial ===
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<CourtResponseDTO> updateCourtPartial(@PathVariable Long id,
                                                               @RequestBody Map<String,Object> updates,
                                                               Authentication auth) {
        CourtResponseDTO updated = courtService.updateCourtPartialIfAllowed(id, updates, auth);
        return ResponseEntity.ok(updated);
    }

}
