package com.application.settleApp.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.application.settleApp.exceptions.AuthenticationFailedException;
import com.application.settleApp.exceptions.RegistrationFailedException;
import com.application.settleApp.security.AuthRequest;
import com.application.settleApp.security.AuthResponse;
import com.application.settleApp.security.RegistrationRequest;
import com.application.settleApp.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

class AuthenticationControllerTest {

  @Mock private AuthService authService;

  @InjectMocks private AuthenticationController authenticationController;

  @BeforeEach
  void setUp() {
    openMocks(this);
  }

  @Test
  void authenticateSuccess() {
    AuthRequest request = new AuthRequest("user@example.com", "password");
    when(authService.generateToken(request)).thenReturn("token");

    ResponseEntity<AuthResponse> response = authenticationController.authenticate(request);

    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
  }

  @Test
  void authenticateFailureDueToPasswordMismatch() {
    AuthRequest request = new AuthRequest("user@example.com", "incorrectPassword");
    when(authService.generateToken(request))
        .thenThrow(
            new AuthenticationFailedException(
                "Authentication failed for user: " + request.getEmail()));

    assertThrows(
        AuthenticationFailedException.class,
        () -> authenticationController.authenticate(request),
        "Expected AuthenticationFailedException to be thrown due to password mismatch");
  }

  @Test
  void registerSuccess() {
    RegistrationRequest request = new RegistrationRequest("user@example.com", "somePassword");
    when(authService.registerUser(request)).thenReturn("token");

    assert authenticationController.register(request).getBody().getToken().equals("token");
  }

  @Test
  void registerFailureDueToEmailAlreadyPresent() {
    RegistrationRequest request = new RegistrationRequest("user@example.com", "incorrectPassword");
    when(authService.registerUser(request))
        .thenThrow(
            new RegistrationFailedException("Registration failed for user: " + request.getEmail()));

    assertThrows(
        RegistrationFailedException.class,
        () -> authenticationController.register(request),
        "Expected RegistrationFailedException to be thrown due to email already present");
  }
}
