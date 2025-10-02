package com.padel.app.controller;

import com.padel.app.dto.CourtDTO;
import com.padel.app.dto.CourtResponseDTO;
import com.padel.app.service.CourtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courts")
public class CourtController {

    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @GetMapping
    public ResponseEntity<List<CourtResponseDTO>> getAllCourts() {
        return ResponseEntity.ok(courtService.getAllCourts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourtResponseDTO> getCourtById(@PathVariable Long id) {
        return courtService.getCourtById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CourtResponseDTO> createCourt(@Valid @RequestBody CourtDTO courtDTO) {
        return ResponseEntity.ok(courtService.createCourt(courtDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourt(@PathVariable Long id) {
        courtService.deleteCourt(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourtResponseDTO> updateCourt(
            @PathVariable Long id,
            @Valid @RequestBody CourtDTO dto
    ) {
        return ResponseEntity.ok(courtService.updateCourt(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CourtResponseDTO> updateCourtPartial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates
    ) {
        return ResponseEntity.ok(courtService.updateCourtPartial(id, updates));
    }

}
