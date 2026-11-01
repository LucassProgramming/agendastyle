package com.agendastyle.backend.auth.dto;

import com.agendastyle.backend.auth.Role;

public record LoginResponse(String token, String tokenType, long expiresIn, String email, Role role) {
}
