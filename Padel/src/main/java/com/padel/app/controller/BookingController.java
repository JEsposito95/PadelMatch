package com.padel.app.controller;

import com.padel.app.dto.booking.BookingDTO;
import com.padel.app.dto.booking.BookingResponseDTO;
import com.padel.app.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings(Authentication auth) {
        List<BookingResponseDTO> bookings = bookingService.getAllBookings(auth);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER','USER')")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Long id, Authentication auth) {
        return bookingService.getBookingById(id, auth)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener las reservas de un Usuario
    @GetMapping("/my-bookings")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER','USER')")
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status
    ) {
        Page<BookingResponseDTO> res = bookingService.getBookingsByAuthenticatedUser(page, size, status);
        return ResponseEntity.ok(res.getContent());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingDTO dto) {
        BookingResponseDTO created = bookingService.createBooking(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER') or isAuthenticated()") // el servicio validará si un USER es dueño
    public ResponseEntity<BookingResponseDTO> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingDTO dto
    ) {
        return ResponseEntity.ok(bookingService.updateBooking(id, dto));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER') or isAuthenticated()") // el servicio validará si un USER es dueño
    public ResponseEntity<BookingResponseDTO> updateBookingPartial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates
    ) {
        return ResponseEntity.ok(bookingService.updateBookingPartial(id, updates));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER') or isAuthenticated()") // el servicio validará si un USER es dueño
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable Long id) {
        BookingResponseDTO updated = bookingService.cancelBooking(id);
        return ResponseEntity.ok(updated);
    }


}
