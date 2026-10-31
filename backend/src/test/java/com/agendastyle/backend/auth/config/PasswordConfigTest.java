package com.agendastyle.backend.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordConfigTest {

    @Test
    void shouldProvideBCryptPasswordEncoder() {
        PasswordEncoder passwordEncoder = new PasswordConfig().passwordEncoder();
        String hash = passwordEncoder.encode("AgendaStyle-2026");

        assertTrue(passwordEncoder.matches("AgendaStyle-2026", hash));
        assertFalse(passwordEncoder.matches("incorrect", hash));
    }
}
