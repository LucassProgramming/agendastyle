package com.agendastyle.backend.client;

import com.agendastyle.backend.client.dto.CreateClientRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptValidRequest() {
        assertTrue(validator.validate(validRequest()).isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldRejectMissingOrBlankFirstName(String firstName) {
        CreateClientRequest request = new CreateClientRequest(
                firstName, "Ruiz", "600000000", "ana@example.com");

        assertHasViolation(request, "firstName");
    }

    @Test
    void shouldRejectFirstNameLongerThanOneHundredCharacters() {
        CreateClientRequest request = new CreateClientRequest(
                "a".repeat(101), "Ruiz", "600000000", "ana@example.com");

        assertHasViolation(request, "firstName");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldRejectMissingOrBlankLastName(String lastName) {
        CreateClientRequest request = new CreateClientRequest(
                "Ana", lastName, "600000000", "ana@example.com");

        assertHasViolation(request, "lastName");
    }

    @Test
    void shouldRejectLastNameLongerThanOneHundredCharacters() {
        CreateClientRequest request = new CreateClientRequest(
                "Ana", "a".repeat(101), "600000000", "ana@example.com");

        assertHasViolation(request, "lastName");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldRejectMissingOrBlankPhone(String phone) {
        CreateClientRequest request = new CreateClientRequest(
                "Ana", "Ruiz", phone, "ana@example.com");

        assertHasViolation(request, "phone");
    }

    @Test
    void shouldRejectPhoneLongerThanThirtyCharacters() {
        CreateClientRequest request = new CreateClientRequest(
                "Ana", "Ruiz", "1".repeat(31), "ana@example.com");

        assertHasViolation(request, "phone");
    }

    @Test
    void shouldRejectInvalidEmail() {
        CreateClientRequest request = new CreateClientRequest(
                "Ana", "Ruiz", "600000000", "ana@example");

        assertHasViolation(request, "email");
    }

    @Test
    void shouldNormalizeEmailAndSetRegistrationDateInEntity() {
        Client client = new Client("Ana", "Ruiz", "600000000", "  ANA@Example.COM  ");

        assertEquals("ana@example.com", client.getEmail());
        assertNotNull(client.getRegistrationDate());
    }

    private static CreateClientRequest validRequest() {
        return new CreateClientRequest("Ana", "Ruiz", "600000000", "ana@example.com");
    }

    private static void assertHasViolation(CreateClientRequest request, String property) {
        assertTrue(validator.validate(request).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals(property)));
    }
}
