package com.agendastyle.backend.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.agendastyle.backend.staff.exception.EmployeeEmailAlreadyExistsException;
import com.agendastyle.backend.staff.exception.InvalidServiceSelectionException;

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
}