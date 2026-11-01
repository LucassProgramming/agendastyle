package com.agendastyle.backend.appointment;

import com.agendastyle.backend.appointment.dto.AppointmentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClientAppointmentControllerTest {

    @Mock
    private AppointmentApplicationService appointmentApplicationService;

    @Mock
    private Authentication authentication;

    private ClientAppointmentController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ClientAppointmentController(appointmentApplicationService);
        when(authentication.getName()).thenReturn("ana@example.com");
    }

    @Test
    void shouldFindAppointmentsUsingAuthenticatedEmail() {
        when(appointmentApplicationService.findAppointmentsForClient("ana@example.com")).thenReturn(List.of());

        ResponseEntity<List<AppointmentResponse>> response = controller.findMyAppointments(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(appointmentApplicationService).findAppointmentsForClient("ana@example.com");
    }

    @Test
    void shouldCancelAppointmentUsingAuthenticatedEmail() {
        AppointmentResponse appointment = org.mockito.Mockito.mock(AppointmentResponse.class);
        when(appointmentApplicationService.cancelClientAppointment(1L, "ana@example.com")).thenReturn(appointment);

        ResponseEntity<AppointmentResponse> response = controller.cancelMyAppointment(1L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(appointment, response.getBody());
        verify(appointmentApplicationService).cancelClientAppointment(1L, "ana@example.com");
    }
}
