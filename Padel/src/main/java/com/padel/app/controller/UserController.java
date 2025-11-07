package com.padel.app.controller;

import com.padel.app.dto.user.UserDTO;
import com.padel.app.dto.user.UserResponseDTO;
import com.padel.app.dto.user.UserRoleUpdateDTO;
import com.padel.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Solo ADMIN y OWNER pueden ver todos los usuarios
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(Authentication auth) {
        return ResponseEntity.ok(userService.getAllUsers(auth));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(userService.getUserById(id, auth));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserDTO dto, Authentication auth) {
        UserResponseDTO created = userService.createUser(dto, auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication auth) {
        userService.deleteUser(id, auth);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO dto,
            Authentication auth
    ) {
        return ResponseEntity.ok(userService.updateUser(id, dto, auth));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleUpdateDTO dto,
            Authentication auth
    ) {
        userService.updateUserRole(id, dto, auth);
        return ResponseEntity.ok("Rol actualizado correctamente.");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUserPartial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates,
            Authentication auth
    ) {
        return ResponseEntity.ok(userService.updateUserPartial(id, updates, auth));
    }

}
