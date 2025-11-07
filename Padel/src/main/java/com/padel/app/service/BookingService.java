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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public List<BookingResponseDTO> getAllBookings(Authentication auth) {
        User authUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        if (authUser.getRole() == User.Role.USER) {
            throw new AccessDeniedException("No tienes permiso para ver todas las reservas.");
        }

        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public Optional<BookingResponseDTO> getBookingById(Long id, Authentication auth) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada"));

        User authUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        if (authUser.getRole() == User.Role.ADMIN) {
            return Optional.of(mapToResponseDTO(booking));
        }
        if (authUser.getRole() == User.Role.OWNER) {
            if (booking.getCourt().getOwner().getIdUser().equals(authUser.getIdUser())) {
                return Optional.of(mapToResponseDTO(booking));
            }
            throw new AccessDeniedException("No tienes permiso para ver esta reserva.");
        }
        // USER: puede ver solo su propia reserva
        if (booking.getCreatedBy().getIdUser().equals(authUser.getIdUser())) {
            return Optional.of(mapToResponseDTO(booking));
        }
        throw new AccessDeniedException("No tienes permiso para ver esta reserva.");
    }

    @Transactional
    public BookingResponseDTO createBooking(BookingDTO dto) {
        log.info("Intentando crear reserva: court={}, start={}, end={}",
                dto.idCourt(), dto.startTime(), dto.endTime());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) && user.getIdUser() != null) {
            user = userRepository.findById(user.getIdUser()).orElseThrow(() -> new EntityNotFoundException("Usuario destino no encontrado"));
        }

        Court court = getCourt(dto.idCourt());

        validateBookingDates(dto.startTime(), dto.endTime());
        validateCourtAvailability(court, dto.startTime(), dto.endTime());

        Booking booking = new Booking(court, user, dto.startTime(), dto.endTime());
        Booking saved = bookingRepository.save(booking);

        log.info("Reserva creada exitosamente: bookingId={}, court={}, user={}",
                saved.getIdBooking(), court.getNameCourt(), user.getNameUser());
        return mapToResponseDTO(saved);
    }

    private void validateBookingDates(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin.");
        }
    }

    private Court getCourt(Long courtId) {
        return courtRepository.findById(courtId)
                .orElseThrow(() -> new EntityNotFoundException("La cancha no existe."));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("El usuario no existe."));
    }

    private void validateCourtAvailability(Court court, LocalDateTime start, LocalDateTime end) {
        boolean overlapping = bookingRepository.existsByCourtAndTimeRange(court.getIdCourt(), start, end);
        if (overlapping) {
            throw new IllegalArgumentException("La cancha no está disponible en ese horario.");
        }
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    @Transactional
    public BookingResponseDTO updateBooking(Long id, BookingDTO dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (dto.startTime().isAfter(dto.endTime())) {
            throw new RuntimeException("La hora de inicio debe ser anterior a la hora de fin.");
        }

        Court court = courtRepository.findById(dto.idCourt())
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        if (!user.getRole().equals(User.Role.ADMIN) && !user.getRole().equals(User.Role.OWNER)) {
            if (!booking.getCreatedBy().getIdUser().equals(user.getIdUser())) {
                throw new AccessDeniedException("No puedes modificar reservas de otros usuarios.");
            }
        }

        booking.setCourt(court);
        booking.setCreatedBy(user);
        booking.setStartTime(dto.startTime());
        booking.setEndTime(dto.endTime());

        System.out.printf("Reserva %d actualizada: Cancha=%s Usuario=%s Inicio=%s Fin=%s%n",
                booking.getIdBooking(), court.getNameCourt(), user.getNameUser(),
                booking.getStartTime(), booking.getEndTime());

        return mapToResponseDTO(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponseDTO updateBookingPartial(Long id, Map<String, Object> updates) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (authUser.getRole() == User.Role.ADMIN) {
            // OK
        } else if (authUser.getRole() == User.Role.OWNER) {
            if (!booking.getCourt().getOwner().getIdUser().equals(authUser.getIdUser())) {
                throw new AccessDeniedException("No tienes permiso para modificar/cancelar esta reserva.");
            }
        } else { // USER
            if (!booking.getCreatedBy().getIdUser().equals(authUser.getIdUser())) {
                throw new AccessDeniedException("No tienes permiso para modificar/cancelar la reserva de otro usuario.");
            }
        }

        updates.forEach((key, value) -> {
            switch (key) {
                case "courtId" -> {
                    Long courtId = Long.parseLong(value.toString());
                    Court court = courtRepository.findById(courtId)
                            .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));
                    booking.setCourt(court);
                }
                case "userId" -> {
                    Long userId = Long.parseLong(value.toString());
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Usuario no existe"));
                    booking.setCreatedBy(user);
                }
                case "startTime" -> booking.setStartTime(LocalDateTime.parse(value.toString()));
                case "endTime" -> booking.setEndTime(LocalDateTime.parse(value.toString()));
                case "status" -> booking.setStatus(Booking.Status.valueOf(value.toString().toUpperCase()));
                default -> throw new RuntimeException("Campo no permitido: " + key);
            }
        });

        if (booking.getStartTime().isAfter(booking.getEndTime())) {
            throw new RuntimeException("Tiempo de inicio debe ser anterior al tiempo de finalización.");
        }

        System.out.printf("Reserva %d parcialmente actualizada: %s%n", booking.getIdBooking(), updates);

        return mapToResponseDTO(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponseDTO cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada"));

        if (booking.getStatus() == Booking.Status.CANCELLED) {
            throw new IllegalStateException("La reserva ya fue cancelada.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        if (!user.getRole().equals(User.Role.ADMIN) && !user.getRole().equals(User.Role.OWNER)) {
            if (!booking.getCreatedBy().getIdUser().equals(user.getIdUser())) {
                throw new AccessDeniedException("No puedes cancelar reservas de otros usuarios.");
            }
        }

        booking.setStatus(Booking.Status.CANCELLED);
        log.info("Reserva cancelada: bookingId={}, user={}",
                booking.getIdBooking(), booking.getCreatedBy().getEmail());

        return mapToResponseDTO(bookingRepository.save(booking));
    }

    //Obtener las reservas del Usuario logueado
    public List<BookingResponseDTO> getBookingsByAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        return bookingRepository.findByCreatedBy(user)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    //Paginación
    public Page<BookingResponseDTO> getBookingsByAuthenticatedUser(int page, int size, String statusFilter) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
        Page<Booking> bookings;

        if (statusFilter != null && !statusFilter.isBlank()) {
            try {
                Booking.Status status = Booking.Status.valueOf(statusFilter.toUpperCase());
                bookings = bookingRepository.findByCreatedByAndStatus(user, status, pageable);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Estado inválido. Usa BOOKED, CANCELLED o COMPLETED.");
            }
        } else {
            bookings = bookingRepository.findByCreatedBy(user, pageable);
        }

        return bookings.map(this::mapToResponseDTO);
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
