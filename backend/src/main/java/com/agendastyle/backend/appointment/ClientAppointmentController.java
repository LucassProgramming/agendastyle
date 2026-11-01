package com.agendastyle.backend.appointment;

import com.agendastyle.backend.appointment.dto.AppointmentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me/appointments")
public class ClientAppointmentController {

    private final AppointmentApplicationService appointmentApplicationService;

    public ClientAppointmentController(AppointmentApplicationService appointmentApplicationService) {
        this.appointmentApplicationService = appointmentApplicationService;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> findMyAppointments(Authentication authentication) {
        return ResponseEntity.ok(appointmentApplicationService.findAppointmentsForClient(authentication.getName()));
    }

    @PatchMapping("/{appointmentId}/cancel")
    public ResponseEntity<AppointmentResponse> cancelMyAppointment(@PathVariable Long appointmentId, Authentication authentication) {
        return ResponseEntity.ok(appointmentApplicationService.cancelClientAppointment(appointmentId, authentication.getName()));
    }
}
