package com.agendastyle.backend.auth;

import com.agendastyle.backend.client.Client;
import com.agendastyle.backend.staff.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserAccountTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void shouldNormalizeEmailAndStartEnabled() {
        UserAccount account = new UserAccount(
                "  ANA@Example.COM  ", passwordEncoder.encode("secret"), Role.CLIENTE);

        assertEquals("ana@example.com", account.getEmail());
        assertEquals(Role.CLIENTE, account.getRole());
        assertTrue(account.isEnabled());
    }

    @Test
    void shouldStoreAHashThatMatchesTheOriginalPassword() {
        String rawPassword = "AgendaStyle-2026";
        String passwordHash = passwordEncoder.encode(rawPassword);
        UserAccount account = new UserAccount("ana@example.com", passwordHash, Role.CLIENTE);

        assertFalse(account.getPasswordHash().equals(rawPassword));
        assertTrue(passwordEncoder.matches(rawPassword, account.getPasswordHash()));
        assertFalse(passwordEncoder.matches("incorrect", account.getPasswordHash()));
    }

    @Test
    void shouldGenerateDifferentHashesForTheSamePasswordBecauseOfTheSalt() {
        String firstHash = passwordEncoder.encode("same-password");
        String secondHash = passwordEncoder.encode("same-password");

        assertFalse(firstHash.equals(secondHash));
        assertTrue(passwordEncoder.matches("same-password", firstHash));
        assertTrue(passwordEncoder.matches("same-password", secondHash));
    }

    @Test
    void shouldLinkClientToItsAccount() {
        UserAccount account = new UserAccount(
                "ana@example.com", passwordEncoder.encode("secret"), Role.CLIENTE);
        Client client = new Client("Ana", "Ruiz", "600000000", "ana@example.com");

        client.linkUserAccount(account);

        assertSame(account, client.getUserAccount());
    }

    @Test
    void shouldLinkEmployeeToItsAccount() {
        UserAccount account = new UserAccount(
                "eva@example.com", passwordEncoder.encode("secret"), Role.EMPLEADO);
        Employee employee = new Employee("Eva", "Sanz", "600000001", "eva@example.com");

        employee.linkUserAccount(account);

        assertSame(account, employee.getUserAccount());
    }

    @Test
    void shouldDefineTheThreePlannedRoles() {
        assertEquals(3, Role.values().length);
        assertEquals(Role.CLIENTE, Role.valueOf("CLIENTE"));
        assertEquals(Role.EMPLEADO, Role.valueOf("EMPLEADO"));
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
    }
}
