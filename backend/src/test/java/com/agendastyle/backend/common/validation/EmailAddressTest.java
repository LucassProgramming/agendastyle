package com.agendastyle.backend.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailAddressTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "user@example.com",
            "name.surname+agenda@sub.domain.com",
            "user-name@example-domain.com",
            "  User@Example.COM  "
    })
    void shouldAcceptValidEmailAddresses(String email) {
        assertTrue(EmailAddress.isValid(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "user",
            "@example.com",
            "user@",
            "user@example",
            "user@@example.com",
            "user @example.com",
            "user@example .com",
            "user..name@example.com",
            ".user@example.com",
            "user.@example.com",
            "user@-example.com",
            "user@example-.com"
    })
    void shouldRejectInvalidEmailAddresses(String email) {
        assertFalse(EmailAddress.isValid(email));
    }

    @Test
    void shouldRejectNullAndBlankEmailAddresses() {
        assertFalse(EmailAddress.isValid(null));
        assertFalse(EmailAddress.isValid("   "));
    }

    @Test
    void shouldNormalizeOuterWhitespaceAndCase() {
        assertEquals("user@example.com", EmailAddress.normalize("  User@Example.COM  "));
    }

    @Test
    void shouldAcceptAtMostOneHundredAndFiftyCharacters() {
        assertTrue(EmailAddress.isValid("a".repeat(138) + "@example.com"));
        assertFalse(EmailAddress.isValid("a".repeat(139) + "@example.com"));
    }
}
