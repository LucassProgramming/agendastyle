package com.agendastyle.backend.auth.dto;

import com.agendastyle.backend.common.validation.ValidEmail;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank @ValidEmail String email, @NotBlank String password) {
}
