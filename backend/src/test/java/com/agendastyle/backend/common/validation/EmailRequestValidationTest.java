package com.agendastyle.backend.common.validation;

import com.agendastyle.backend.client.dto.CreateClientRequest;
import com.agendastyle.backend.staff.dto.CreateEmployeeRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailRequestValidationTest {

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
    void shouldRequireClientEmail() {
        CreateClientRequest request = new CreateClientRequest("Ana", "Ruiz", "600000000", " ");

        assertTrue(validator.validate(request).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldApplyEmailRulesToEmployeeRequests() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Eva", "Sanz", "600000001", "eva@example", Set.of(1L));

        assertTrue(validator.validate(request).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldAcceptOuterWhitespaceBecauseItWillBeNormalized() {
        CreateClientRequest request = new CreateClientRequest(
                "Ana", "Ruiz", "600000000", "  ANA@EXAMPLE.COM  ");

        assertTrue(validator.validate(request).isEmpty());
    }
}
