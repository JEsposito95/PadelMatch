package com.padel.app.service;

import com.padel.app.dto.booking.BookingDTO;
import com.padel.app.dto.booking.BookingResponseDTO;
import com.padel.app.model.Booking;
import com.padel.app.model.Court;
import com.padel.app.model.User;
import com.padel.app.repository.BookingRepository;
import com.padel.app.repository.CourtRepository;
import com.padel.app.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, CourtRepository courtRepository,
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

    @Transactional
    public BookingResponseDTO createBooking(BookingDTO dto) {

        // 1️⃣ Validar fechas
        if (dto.startTime().isAfter(dto.endTime()) || dto.startTime().isEqual(dto.endTime())) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
        }

        // 2️⃣ Verificar que existan cancha y usuario
        Court court = courtRepository.findById(dto.idCourt())
                .orElseThrow(() -> new EntityNotFoundException("La cancha no existe"));

        User user = userRepository.findById(dto.idUser())
                .orElseThrow(() -> new EntityNotFoundException("El usuario no existe"));

        // 3️⃣ Verificar disponibilidad de cancha
        boolean overlapping = bookingRepository.existsByCourtAndTimeRange(
                court.getIdCourt(), dto.startTime(), dto.endTime()
        );

        if (overlapping) {
            throw new IllegalArgumentException("La cancha no está disponible en ese horario");
        }

        // 4️⃣ Crear y guardar
        Booking booking = new Booking();
        booking.setCourt(court);
        booking.setCreatedBy(user);
        booking.setStartTime(dto.startTime());
        booking.setEndTime(dto.endTime());
        booking.setStatus(Booking.Status.BOOKED);

        Booking saved = bookingRepository.save(booking);

        log.info("Reserva creada: usuario={} cancha={} inicio={} fin={}",
                user.getEmail(), court.getNameCourt(), dto.startTime(), dto.endTime());

        return mapToResponseDTO(saved);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    @Transactional
    public BookingResponseDTO updateBooking(Long id, BookingDTO dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (dto.startTime().isAfter(dto.endTime())) {
            throw new RuntimeException("Start time must be before end time");
        }

        Court court = courtRepository.findById(dto.idCourt())
                .orElseThrow(() -> new RuntimeException("Court not found"));

        User user = userRepository.findById(dto.idUser())
                .orElseThrow(() -> new RuntimeException("User not found"));

        booking.setCourt(court);
        booking.setCreatedBy(user);
        booking.setStartTime(dto.startTime());
        booking.setEndTime(dto.endTime());

        System.out.printf("Booking %d updated: Court=%s User=%s Start=%s End=%s%n",
                booking.getIdBooking(), court.getNameCourt(), user.getNameUser(),
                booking.getStartTime(), booking.getEndTime());

        return mapToResponseDTO(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponseDTO updateBookingPartial(Long id, Map<String, Object> updates) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "courtId" -> {
                    Long courtId = Long.parseLong(value.toString());
                    Court court = courtRepository.findById(courtId)
                            .orElseThrow(() -> new RuntimeException("Court not found"));
                    booking.setCourt(court);
                }
                case "userId" -> {
                    Long userId = Long.parseLong(value.toString());
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    booking.setCreatedBy(user);
                }
                case "startTime" -> booking.setStartTime(LocalDateTime.parse(value.toString()));
                case "endTime" -> booking.setEndTime(LocalDateTime.parse(value.toString()));
                case "status" -> booking.setStatus(Booking.Status.valueOf(value.toString().toUpperCase()));
                default -> throw new RuntimeException("Campo no permitido: " + key);
            }
        });

        if (booking.getStartTime().isAfter(booking.getEndTime())) {
            throw new RuntimeException("Start time must be before end time");
        }

        System.out.printf("Booking %d partially updated: %s%n", booking.getIdBooking(), updates);

        return mapToResponseDTO(bookingRepository.save(booking));
    }

    private BookingResponseDTO mapToResponseDTO(Booking booking) {
        return new BookingResponseDTO(
                booking.getIdBooking(),
                booking.getCourt().getIdCourt(),
                booking.getCourt().getNameCourt(),
                booking.getCreatedBy().getIdUser(),
                booking.getCreatedBy().getNameUser(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus().name()
        );
    }
}
