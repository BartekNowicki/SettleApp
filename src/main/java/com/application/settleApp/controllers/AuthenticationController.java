package com.application.settleApp.controllers;

import com.application.settleApp.enums.ExceptionMessage;
import com.application.settleApp.exceptions.AuthenticationFailedException;
import com.application.settleApp.security.AuthRequest;
import com.application.settleApp.security.AuthResponse;
import com.application.settleApp.security.RegistrationRequest;
import com.application.settleApp.services.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@Validated
public class AuthenticationController {

  private final AuthService authService;

  @PostMapping(value = "/authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
    String token = authService.generateToken(request);

    if (token != null) {
      return ResponseEntity.ok(new AuthResponse(token));
    } else {
      throw new AuthenticationFailedException(ExceptionMessage.USER_AUTHENTICATION_FAILED.getMessage());
    }
  }

  @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistrationRequest request) {
    String token = authService.registerUser(request);

    if (token != null) {
      return ResponseEntity.ok(new AuthResponse(token));
    } else {
      throw new AuthenticationFailedException(
          ExceptionMessage.USER_REGISTRATION_FAILED.getMessage());
    }
  }
}
