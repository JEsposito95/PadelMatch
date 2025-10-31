package com.padel.app.service;

import com.padel.app.dto.user.UserDTO;
import com.padel.app.dto.user.UserResponseDTO;
import com.padel.app.model.User;
import com.padel.app.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // === Obtener todos los usuarios ===
    public List<UserResponseDTO> getAllUsers() {
        log.info("Obteniendo todos los usuarios");
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // === Obtener usuario por ID ===
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El usuario con ID " + id + " no existe."));
        return mapToResponseDTO(user);
    }

    // === Crear usuario ===
    @Transactional
    public UserResponseDTO createUser(UserDTO dto) {
        validateEmail(dto.email());

        String encodedPassword = passwordEncoder.encode(dto.password());

        User user = new User(
                null,
                dto.email(),
                encodedPassword,
                dto.nameUser(),
                dto.photoUrl(),
                User.Role.USER,
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        User saved = userRepository.save(user);
        log.info("Usuario creado correctamente: email={}", saved.getEmail());
        return mapToResponseDTO(saved);
    }

    // === Eliminar usuario ===
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("El usuario con ID " + id + " no existe.");
        }
        userRepository.deleteById(id);
        log.info("Usuario eliminado: id={}", id);
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

    // === Validaciones auxiliares ===
    private void validateEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        if (exists) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo electrónico.");
        }
    }

    // === Mapeo manual (sin ModelMapper) ===
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
