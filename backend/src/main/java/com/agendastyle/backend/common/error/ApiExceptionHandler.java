package com.agendastyle.backend.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.agendastyle.backend.staff.exception.EmployeeEmailAlreadyExistsException;
import com.agendastyle.backend.staff.exception.InvalidServiceSelectionException;
import com.agendastyle.backend.client.exception.ClientEmailAlreadyExistsException;
import com.agendastyle.backend.appointment.exception.AppointmentResourceNotFoundException;
import com.agendastyle.backend.appointment.exception.InvalidAppointmentException;
import com.agendastyle.backend.appointment.exception.AppointmentOverlapException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EmployeeEmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmployeeEmailAlreadyExists(EmployeeEmailAlreadyExistsException exception) {
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InvalidServiceSelectionException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidServiceSelection(InvalidServiceSelectionException exception) {
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(ClientEmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleClientEmailAlreadyExists(ClientEmailAlreadyExistsException exception) {
        ApiErrorResponse error = new ApiErrorResponse(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    @ExceptionHandler(AppointmentResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAppointmentResourceNotFound(AppointmentResourceNotFoundException exception) {
        ApiErrorResponse error = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(),HttpStatus.NOT_FOUND.getReasonPhrase(),exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidAppointmentException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidAppointment(InvalidAppointmentException exception) {
        ApiErrorResponse error = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST.getReasonPhrase(),exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    @ExceptionHandler(AppointmentOverlapException.class)
    public ResponseEntity<ApiErrorResponse> handleAppointmentOverlap(AppointmentOverlapException exception) {
        ApiErrorResponse error = new ApiErrorResponse(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), exception.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}