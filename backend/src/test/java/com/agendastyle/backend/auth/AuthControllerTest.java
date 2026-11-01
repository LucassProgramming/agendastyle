package com.agendastyle.backend.auth;

import com.agendastyle.backend.auth.dto.LoginRequest;
import com.agendastyle.backend.auth.dto.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Mock
    private AuthApplicationService authApplicationService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(authApplicationService);
    }

    @Test
    void shouldReturnOkAfterSuccessfulAuthentication() {
        LoginRequest request = new LoginRequest("ana@example.com", "correct-password");
        LoginResponse response = new LoginResponse("signed.jwt.token", "Bearer", 3600L, "ana@example.com", Role.CLIENTE);
        when(authApplicationService.login(request)).thenReturn(response);

        ResponseEntity<LoginResponse> result = authController.login(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
        verify(authApplicationService).login(request);
    }
}
