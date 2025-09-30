package com.padel.app.service;

import com.padel.app.dto.CourtDTO;
import com.padel.app.dto.CourtResponseDTO;
import com.padel.app.model.Court;
import com.padel.app.model.User;
import com.padel.app.repository.CourtRepository;
import com.padel.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CourtService {

    private final CourtRepository courtRepository;
    private final UserRepository userRepository;

    public CourtService(CourtRepository courtRepository, UserRepository userRepository) {
        this.courtRepository = courtRepository;
        this.userRepository = userRepository;
    }

    public List<CourtResponseDTO> getAllCourts() {
        return courtRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public Optional<CourtResponseDTO> getCourtById(Long id) {
        return courtRepository.findById(id).map(this::mapToResponseDTO);
    }

    public CourtResponseDTO createCourt(CourtDTO dto) {
        User owner = userRepository.findById(dto.ownerId())
                .orElseThrow(() -> new RuntimeException("El dueño no existe"));

        Court court = new Court(
                null,
                owner,
                dto.nombre(),
                dto.direccion(),
                dto.lat(),
                dto.lng(),
                dto.price(),
                null
        );

        return mapToResponseDTO(courtRepository.save(court));
    }

    public void deleteCourt(Long id) {
        courtRepository.deleteById(id);
    }

    //Modificación Cancha
    @Transactional
    public CourtResponseDTO updateCourt(Long id, CourtDTO dto) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Court not found"));

        court.setCourtName(dto.nombre());
        court.setDireccion(dto.direccion());
        court.setLat(dto.lat());
        court.setLng(dto.lng());
        court.setPrice(dto.price());

        User owner = userRepository.findById(dto.ownerId())
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
                case "courtName" -> court.setCourtName((String) value);
                case "address" -> court.setDireccion((String) value);
                case "lat" -> court.setLat(Double.parseDouble(value.toString()));
                case "lng" -> court.setLng(Double.parseDouble(value.toString()));
                case "price" -> court.setPrice(Double.parseDouble(value.toString()));
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

    private CourtResponseDTO mapToResponseDTO(Court court) {
        return new CourtResponseDTO(
                court.getId(),
                court.getCourtName(),
                court.getDireccion(),
                court.getLat(),
                court.getLng(),
                court.getPrice(),
                court.getOwner().getId(),
                court.getOwner().getNombre()
        );
    }
}
