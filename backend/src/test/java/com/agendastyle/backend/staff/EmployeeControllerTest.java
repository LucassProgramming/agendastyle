package com.agendastyle.backend.staff;

import com.agendastyle.backend.staff.dto.CreateEmployeeRequest;
import com.agendastyle.backend.staff.dto.EmployeeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmployeeControllerTest {

    @Mock
    private EmployeeApplicationService employeeApplicationService;

    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeController = new EmployeeController(employeeApplicationService);
    }

    @Test
    void shouldReturnCreatedWhenEmployeeIsCreated() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Eva", "Sanz", "600000001", "eva@example.com", Set.of(1L));
        EmployeeResponse response = response(1L, "Eva", "eva@example.com", Set.of(1L));
        when(employeeApplicationService.create(request)).thenReturn(response);

        ResponseEntity<EmployeeResponse> result = employeeController.create(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertSame(response, result.getBody());
        verify(employeeApplicationService).create(request);
    }

    @Test
    void shouldReturnOkWithAllEmployees() {
        List<EmployeeResponse> employees = List.of(
                response(1L, "Eva", "eva@example.com", Set.of(1L)),
                response(2L, "Luis", "luis@example.com", Set.of(1L, 2L)));
        when(employeeApplicationService.findAll()).thenReturn(employees);

        ResponseEntity<List<EmployeeResponse>> result = employeeController.findAll();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(employees, result.getBody());
        verify(employeeApplicationService).findAll();
    }

    private static EmployeeResponse response(Long id, String firstName, String email, Set<Long> serviceIds) {
        return new EmployeeResponse(
                id, firstName, "Sanz", "600000001", email, true, serviceIds);
    }
}
