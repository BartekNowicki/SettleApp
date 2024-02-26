package com.application.settleApp.controllers;

import com.application.settleApp.exceptions.AuthenticationFailedException;
import com.application.settleApp.security.AuthRequest;
import com.application.settleApp.security.AuthResponse;
import com.application.settleApp.services.JwtTokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class AuthenticationController {

  private final JwtTokenService jwtTokenService;

  @PostMapping(value = "/authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
    String token = jwtTokenService.generateToken(request);

    if (token != null) {
      return ResponseEntity.ok(new AuthResponse(token));
    } else {
      throw new AuthenticationFailedException("User authentication failed");
    }
  }
}
