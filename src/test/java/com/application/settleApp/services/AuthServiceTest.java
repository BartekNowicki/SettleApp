package com.application.settleApp.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.application.settleApp.exceptions.AuthenticationFailedException;
import com.application.settleApp.models.BaseEntity;
import com.application.settleApp.models.User;
import com.application.settleApp.repositories.RoleRepository;
import com.application.settleApp.repositories.UserRepository;
import com.application.settleApp.security.AuthRequest;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private AuthService authService;

  private User user;
  private final String rawPassword = "password";
  private final String hashedPassword = "hashedPassword";
  SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
  String base64EncodedSecretKey = Base64.getEncoder().encodeToString(key.getEncoded());

  @BeforeEach
  void setUp() {
    user = BaseEntity.getNewWithDefaultDates(User.class);
    user.setEmail("user@example.com");
    user.setPassword(hashedPassword);

    when(userRepository.findByEmail("user@example.com")).thenReturn(user);
    authService = new AuthService(userRepository, roleRepository, passwordEncoder, base64EncodedSecretKey);
  }

  @Test
  void generateTokenSuccess() {
    AuthRequest request = new AuthRequest("user@example.com", rawPassword);
    when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(true);

    String token = authService.generateToken(request);
    assertNotNull(token);
  }

  @Test
  void authenticateFailureDueToPasswordMismatch() {
    // Prepare the user with the hashed password
    User user = BaseEntity.getNewWithDefaultDates(User.class);
    user.setEmail("user@example.com");
    user.setPassword(hashedPassword);

    when(passwordEncoder.matches("incorrectPassword", user.getPassword())).thenReturn(false);
    when(userRepository.findByEmail("user@example.com")).thenReturn(user);

    AuthRequest request = new AuthRequest("user@example.com", "incorrectPassword");

    assertThrows(
        AuthenticationFailedException.class,
        () -> {
          authService.generateToken(request);
        },
        "Expected AuthenticationFailedException to be thrown due to password mismatch");
  }
}
