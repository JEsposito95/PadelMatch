package com.padel.app.service;

import com.padel.app.dto.BookingDTO;
import com.padel.app.dto.BookingResponseDTO;
import com.padel.app.model.Booking;
import com.padel.app.model.Court;
import com.padel.app.model.User;
import com.padel.app.repository.BookingRepository;
import com.padel.app.repository.CourtRepository;
import com.padel.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          CourtRepository courtRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.courtRepository = courtRepository;
        this.userRepository = userRepository;
    }

    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public Optional<BookingResponseDTO> getBookingById(Long id) {
        return bookingRepository.findById(id).map(this::mapToResponseDTO);
    }

    public BookingResponseDTO createBooking(BookingDTO dto) {
        Court court = courtRepository.findById(dto.courtId())
                .orElseThrow(() -> new RuntimeException("La cancha con id " + dto.courtId() + " no existe"));

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("El usuario con id " + dto.userId() + " no existe"));

        Booking booking = new Booking();
        booking.setCourt(court);
        booking.setCreatedBy(user);
        booking.setStartTime(dto.startTime());
        booking.setEndTime(dto.endTime());
        booking.setStatus(Booking.Status.BOOKED);

        return mapToResponseDTO(bookingRepository.save(booking));
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    private BookingResponseDTO mapToResponseDTO(Booking booking) {
        return new BookingResponseDTO(
                booking.getId(),
                booking.getCourt().getId(),
                booking.getCourt().getCourtName(),
                booking.getCreatedBy().getId(),
                booking.getCreatedBy().getNombre(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus().name()
        );
    }
}
