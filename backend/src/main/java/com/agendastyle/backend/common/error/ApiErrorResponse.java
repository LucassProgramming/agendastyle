package com.agendastyle.backend.common.error;

public record ApiErrorResponse(
        int status,
        String error,
        String message
) {
}