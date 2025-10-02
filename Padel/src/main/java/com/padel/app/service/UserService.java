package com.padel.app.service;

import com.padel.app.dto.UserDTO;
import com.padel.app.dto.UserResponseDTO;
import com.padel.app.model.User;
import com.padel.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
                dto.nameUser(),
                dto.photoUrl(),
                User.Role.USER,
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        return mapToResponseDTO(userRepository.save(user));
    }

    //Eliminar Usuario
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    //Modificar usuario completo
    @Transactional
    public UserResponseDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setNameUser(dto.nameUser());
        user.setEmail(dto.email());
        user.setPassword(dto.password()); // ⚠️ luego encriptamos
        user.setPhotoUrl(dto.photoUrl());

        return mapToResponseDTO(userRepository.save(user));
    }

    //Modificar usuario parcialmente
    @Transactional
    public UserResponseDTO updateUserPartial(Long id, Map<String, Object> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        updates.forEach((key, value) -> {
            switch (key) {
                case "nombre" -> user.setNameUser((String) value);
                case "email" -> user.setEmail((String) value);
                case "password" -> user.setPassword((String) value);
                case "fotoUrl" -> user.setPhotoUrl((String) value);
                default -> throw new RuntimeException("Campo no permitido: " + key);
            }
        });

        return mapToResponseDTO(userRepository.save(user));
    }


    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getIdUser(),
                user.getNameUser(),
                user.getEmail(),
                user.getPhotoUrl(),
                user.getRole().name(),
                user.getPoints()
        );
    }
}
