package com.padel.app.service;

import com.padel.app.dto.UserDTO;
import com.padel.app.dto.UserResponseDTO;
import com.padel.app.model.User;
import com.padel.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id).map(this::mapToResponseDTO);
    }

    public UserResponseDTO createUser(UserDTO dto) {
        User user = new User(
                null,
                dto.email(),
                dto.password(),
                dto.nombre(),
                dto.fotoUrl(),
                User.Role.USER,
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        return mapToResponseDTO(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getNombre(),
                user.getEmail(),
                user.getFotoUrl(),
                user.getRole().name(),
                user.getPuntos()
        );
    }
}
