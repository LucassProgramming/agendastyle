package com.agendastyle.backend.auth.security;

import com.agendastyle.backend.auth.Role;
import com.agendastyle.backend.auth.UserAccount;
import com.agendastyle.backend.auth.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAccountDetailsServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    private UserAccountDetailsService userAccountDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userAccountDetailsService = new UserAccountDetailsService(userAccountRepository);
    }

    @Test
    void shouldLoadAccountUsingNormalizedEmail() {
        UserAccount account = new UserAccount(
                "ana@example.com", "$2a$10$encoded-password", Role.CLIENTE);
        when(userAccountRepository.findByEmailIgnoreCase("ana@example.com"))
                .thenReturn(Optional.of(account));

        UserDetails userDetails = userAccountDetailsService
                .loadUserByUsername("  ANA@Example.COM  ");

        assertEquals("ana@example.com", userDetails.getUsername());
        assertEquals(account.getPasswordHash(), userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CLIENTE")));
        verify(userAccountRepository).findByEmailIgnoreCase("ana@example.com");
    }

    @Test
    void shouldRejectUnknownAccountWithoutRevealingTheEmail() {
        when(userAccountRepository.findByEmailIgnoreCase("unknown@example.com"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userAccountDetailsService.loadUserByUsername("unknown@example.com"));

        assertEquals("User account not found", exception.getMessage());
    }
}
