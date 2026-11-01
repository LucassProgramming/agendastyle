package com.agendastyle.backend.auth;

import com.agendastyle.backend.auth.dto.LoginRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginRequestValidationTest {

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
    void shouldAcceptValidCredentials() {
        assertTrue(validator.validate(new LoginRequest("ana@example.com", "correct-password")).isEmpty());
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertHasViolation(new LoginRequest("ana@example", "password"), "email");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldRejectMissingOrBlankPassword(String password) {
        assertHasViolation(new LoginRequest("ana@example.com", password), "password");
    }

    private static void assertHasViolation(LoginRequest request, String property) {
        assertTrue(validator.validate(request).stream().anyMatch(violation -> violation.getPropertyPath().toString().equals(property)));
    }
}
