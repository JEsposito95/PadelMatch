package com.padel.app.service;

import com.padel.app.dto.CourtDTO;
import com.padel.app.dto.CourtResponseDTO;
import com.padel.app.model.Court;
import com.padel.app.model.User;
import com.padel.app.repository.CourtRepository;
import com.padel.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

    private CourtResponseDTO mapToResponseDTO(Court court) {
        return new CourtResponseDTO(
                court.getId(),
                court.getNombre(),
                court.getDireccion(),
                court.getLat(),
                court.getLng(),
                court.getPrice(),
                court.getOwner().getId(),
                court.getOwner().getNombre()
        );
    }
}
