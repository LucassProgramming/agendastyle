package com.agendastyle.backend.staff;

import com.agendastyle.backend.catalog.SalonService;
import com.agendastyle.backend.staff.dto.CreateEmployeeRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeValidationTest {

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
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                firstName, "Sanz", "600000001", "eva@example.com", Set.of(1L));

        assertHasViolation(request, "firstName");
    }

    @Test
    void shouldRejectFirstNameLongerThanOneHundredCharacters() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "a".repeat(101), "Sanz", "600000001", "eva@example.com", Set.of(1L));

        assertHasViolation(request, "firstName");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldRejectMissingOrBlankLastName(String lastName) {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Eva", lastName, "600000001", "eva@example.com", Set.of(1L));

        assertHasViolation(request, "lastName");
    }

    @Test
    void shouldRejectLastNameLongerThanOneHundredCharacters() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Eva", "a".repeat(101), "600000001", "eva@example.com", Set.of(1L));

        assertHasViolation(request, "lastName");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldRejectMissingOrBlankPhone(String phone) {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Eva", "Sanz", phone, "eva@example.com", Set.of(1L));

        assertHasViolation(request, "phone");
    }

    @Test
    void shouldRejectPhoneLongerThanThirtyCharacters() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Eva", "Sanz", "1".repeat(31), "eva@example.com", Set.of(1L));

        assertHasViolation(request, "phone");
    }

    @Test
    void shouldRejectInvalidEmail() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Eva", "Sanz", "600000001", "eva@example", Set.of(1L));

        assertHasViolation(request, "email");
    }

    @Test
    void shouldRejectMissingServiceIds() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Eva", "Sanz", "600000001", "eva@example.com", null);

        assertHasViolation(request, "serviceIds");
    }

    @Test
    void shouldRejectEmptyServiceIds() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Eva", "Sanz", "600000001", "eva@example.com", Set.of());

        assertHasViolation(request, "serviceIds");
    }

    @Test
    void shouldNormalizeEmailAndStartActive() {
        Employee employee = new Employee("Eva", "Sanz", "600000001", "  EVA@Example.COM  ");

        assertEquals("eva@example.com", employee.getEmail());
        assertTrue(employee.isActive());
    }

    @Test
    void shouldAssignServicesWithoutRemovingExistingOnes() {
        Employee employee = new Employee("Eva", "Sanz", "600000001", "eva@example.com");
        SalonService firstService = org.mockito.Mockito.mock(SalonService.class);
        SalonService secondService = org.mockito.Mockito.mock(SalonService.class);

        employee.assignServices(Set.of(firstService));
        employee.assignServices(Set.of(secondService));

        assertEquals(Set.of(firstService, secondService), employee.getServices());
    }

    private static CreateEmployeeRequest validRequest() {
        return new CreateEmployeeRequest(
                "Eva", "Sanz", "600000001", "eva@example.com", Set.of(1L));
    }

    private static void assertHasViolation(CreateEmployeeRequest request, String property) {
        assertTrue(validator.validate(request).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals(property)));
    }
}
