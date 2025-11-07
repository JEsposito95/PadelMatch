package com.padel.app.dto.user;

import com.padel.app.model.User;
import jakarta.validation.constraints.NotNull;

public record UserRoleUpdateDTO(
        @NotNull(message = "El rol es obligatorio")
        User.Role role
) {}
