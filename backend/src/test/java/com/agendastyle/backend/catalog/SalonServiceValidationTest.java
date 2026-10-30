package com.agendastyle.backend.catalog;

import com.agendastyle.backend.catalog.dto.CreateSalonServiceRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalonServiceValidationTest {

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
    void shouldRejectMissingOrBlankName(String name) {
        CreateSalonServiceRequest request = new CreateSalonServiceRequest(
                name, "Descripción", 30, new BigDecimal("20.00"));

        assertHasViolation(request, "name");
    }

    @Test
    void shouldRejectNameLongerThanOneHundredCharacters() {
        CreateSalonServiceRequest request = new CreateSalonServiceRequest(
                "a".repeat(101), "Descripción", 30, new BigDecimal("20.00"));

        assertHasViolation(request, "name");
    }

    @Test
    void shouldRejectDescriptionLongerThanFiveHundredCharacters() {
        CreateSalonServiceRequest request = new CreateSalonServiceRequest(
                "Corte", "a".repeat(501), 30, new BigDecimal("20.00"));

        assertHasViolation(request, "description");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void shouldRejectMissingZeroOrNegativeDuration(Integer durationMinutes) {
        CreateSalonServiceRequest request = new CreateSalonServiceRequest(
                "Corte", "Descripción", durationMinutes, new BigDecimal("20.00"));

        assertHasViolation(request, "durationMinutes");
    }

    @Test
    void shouldRejectMissingPrice() {
        CreateSalonServiceRequest request = new CreateSalonServiceRequest(
                "Corte", "Descripción", 30, null);

        assertHasViolation(request, "price");
    }

    @Test
    void shouldRejectNegativePrice() {
        CreateSalonServiceRequest request = new CreateSalonServiceRequest(
                "Corte", "Descripción", 30, new BigDecimal("-0.01"));

        assertHasViolation(request, "price");
    }

    @Test
    void shouldAcceptFreeServiceAccordingToCurrentContract() {
        CreateSalonServiceRequest request = new CreateSalonServiceRequest(
                "Consulta", null, 15, BigDecimal.ZERO);

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void shouldCreateServicesActiveByDefault() {
        SalonService service = new SalonService("Corte", null, 30, new BigDecimal("20.00"));

        assertTrue(service.isActive());
    }

    private static CreateSalonServiceRequest validRequest() {
        return new CreateSalonServiceRequest(
                "Corte", "Corte y peinado", 45, new BigDecimal("25.50"));
    }

    private static void assertHasViolation(CreateSalonServiceRequest request, String property) {
        assertFalse(validator.validate(request).isEmpty());
        assertTrue(validator.validate(request).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals(property)));
    }
}
