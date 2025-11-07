package com.padel.app.service;

import com.padel.app.dto.court.CourtDTO;
import com.padel.app.dto.court.CourtResponseDTO;
import com.padel.app.model.Booking;
import com.padel.app.model.Court;
import com.padel.app.model.User;
import com.padel.app.repository.BookingRepository;
import com.padel.app.repository.CourtRepository;
import com.padel.app.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourtService {

    private static final Logger log = LoggerFactory.getLogger(CourtService.class);
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public CourtService(CourtRepository courtRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.courtRepository = courtRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<CourtResponseDTO> getAllCourts() {
        log.info("Obteniendo todas las canchas");
        return courtRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public CourtResponseDTO getCourtById(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La cancha con ID " + id + " no existe."));
        return mapToResponseDTO(court);
    }

    // === Crear cancha ===
    @Transactional
    public CourtResponseDTO createCourt(CourtDTO dto, Authentication auth) {
        User authUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        // Solo OWNER o ADMIN pueden crear
        if (authUser.getRole() == User.Role.USER) {
            throw new AccessDeniedException("No tienes permiso para crear una cancha.");
        }

        if (dto.price().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El precio debe ser mayor que 0.");

        Court court = new Court(
                null,
                authUser, // 🔥 el dueño será el usuario autenticado
                dto.nameCourt(),
                dto.direction(),
                dto.lat(),
                dto.lng(),
                dto.price(),
                null
        );

        courtRepository.save(court);
        log.info("Cancha creada por {} - {}", authUser.getEmail(), dto.nameCourt());

        return mapToResponseDTO(court);
    }

    //Modificación Parcial
    @Transactional
    public CourtResponseDTO updateCourtPartialIfAllowed(Long id, Map<String, Object> updates, Authentication auth) {
        User authUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));

        validateOwnershipOrAdmin(authUser, court);

        try {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "nameCourt" -> court.setNameCourt((String) value);
                    case "direction" -> court.setDirection((String) value);
                    case "lat" -> court.setLat(Double.parseDouble(value.toString()));
                    case "lng" -> court.setLng(Double.parseDouble(value.toString()));
                    case "price" -> court.setPrice(new BigDecimal(value.toString()));
                    // no permitir cambiar owner desde partial
                    default -> throw new IllegalArgumentException("Campo no permitido: " + key);
                }
            });
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato de número inválido en los campos numéricos.");
        }

        return mapToResponseDTO(courtRepository.save(court));
    }

    // === Actualizar cancha ===
    @Transactional
    public CourtResponseDTO updateCourtIfAllowed(Long id, CourtDTO dto, Authentication auth) {
        User authUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));

        // 🔐 Validar si el owner está intentando modificar su propia cancha
        validateOwnershipOrAdmin(authUser, court);

        // ADMIN puede modificar cualquier cancha
        court.setNameCourt(dto.nameCourt());
        court.setDirection(dto.direction());
        court.setLat(dto.lat());
        court.setLng(dto.lng());
        court.setPrice(dto.price());

        courtRepository.save(court);
        log.info("Cancha actualizada por {} - {}", authUser.getEmail(), dto.nameCourt());

        return mapToResponseDTO(court);
    }

    // === Eliminar cancha ===
    @Transactional
    public void deleteCourtIfAllowed(Long id, Authentication auth) {
        User authUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));

        if (!court.getBookings().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar una cancha con reservas activas.");
        }

        validateOwnershipOrAdmin(authUser, court);

        courtRepository.delete(court);
        log.info("Cancha eliminada por {} - {}", authUser.getEmail(), court.getNameCourt());
    }

    //Disponibilidad de canchas
    public List<CourtResponseDTO> getAvailableCourts(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("Buscando disponibilidad entre {} y {}", startTime, endTime);

        // Traemos todas las canchas
        List<Court> allCourts = courtRepository.findAll();

        // Filtramos las que no tienen solapamientos
        List<Court> availableCourts = allCourts.stream()
                .filter(court -> {
                    List<Booking> overlaps = bookingRepository.findOverlappingBookings(
                            court.getIdCourt(), startTime, endTime);
                    return overlaps.isEmpty();
                })
                .collect(Collectors.toList());

        return availableCourts.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private CourtResponseDTO mapToResponseDTO(Court court) {
        return new CourtResponseDTO(
                court.getIdCourt(),
                court.getNameCourt(),
                court.getDirection(),
                court.getLat(),
                court.getLng(),
                court.getPrice(),
                court.getOwner().getIdUser(),
                court.getOwner().getNameUser()
        );
    }

    private void validateOwnershipOrAdmin(User authUser, Court court) {
        if (authUser.getRole() == User.Role.OWNER &&
                !court.getOwner().getIdUser().equals(authUser.getIdUser())) {
            throw new AccessDeniedException("No tienes permiso para modificar esta cancha.");
        }
    }

}
