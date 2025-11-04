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

    @Transactional
    public CourtResponseDTO createCourt(CourtDTO dto) {
        User owner = userRepository.findById(dto.idOwner())
                .orElseThrow(() -> new RuntimeException("El dueño no existe"));

        Court court = new Court(
                null,
                owner,
                dto.nameCourt(),
                dto.direction(),
                dto.lat(),
                dto.lng(),
                dto.price(),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Court saved = courtRepository.save(court);
        log.info("Cancha creada: id={}, nombre={}", saved.getIdCourt(), saved.getNameCourt());
        return mapToResponseDTO(saved);
    }

    @Transactional
    public void deleteCourt(Long id) {
        if (!courtRepository.existsById(id)) {
            throw new EntityNotFoundException("La cancha con ID " + id + " no existe.");
        }
        courtRepository.deleteById(id);
        log.info("Cancha eliminada: id={}", id);
    }

    //Modificación Cancha
    @Transactional
    public CourtResponseDTO updateCourt(Long id, CourtDTO dto) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Court not found"));

        court.setNameCourt(dto.nameCourt());
        court.setDirection(dto.direction());
        court.setLat(dto.lat());
        court.setLng(dto.lng());
        court.setPrice(dto.price());

        User owner = userRepository.findById(dto.idOwner())
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        court.setOwner(owner);

        return mapToResponseDTO(courtRepository.save(court));
    }

    //Modificación Parcial
    @Transactional
    public CourtResponseDTO updateCourtPartial(Long id, Map<String, Object> updates) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Court not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "courtName" -> court.setNameCourt((String) value);
                case "address" -> court.setDirection((String) value);
                case "lat" -> court.setLat(Double.parseDouble(value.toString()));
                case "lng" -> court.setLng(Double.parseDouble(value.toString()));
                case "price" -> court.setPrice(new BigDecimal(value.toString()));
                case "ownerId" -> {
                    Long ownerId = Long.parseLong(value.toString());
                    User owner = userRepository.findById(ownerId)
                            .orElseThrow(() -> new RuntimeException("Owner not found"));
                    court.setOwner(owner);
                }
                default -> throw new RuntimeException("Campo no permitido: " + key);
            }
        });

        return mapToResponseDTO(courtRepository.save(court));
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
}
