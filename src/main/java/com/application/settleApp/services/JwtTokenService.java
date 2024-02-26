package com.application.settleApp.services;

import com.application.settleApp.exceptions.AuthenticationFailedException;
import com.application.settleApp.models.Role;
import com.application.settleApp.models.User;
import com.application.settleApp.repositories.UserRepository;
import com.application.settleApp.security.AuthRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
public class JwtTokenService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final String secretKey;

  public String generateToken(AuthRequest request) {
    User user = userRepository.findByEmail(request.getEmail());
    if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new AuthenticationFailedException(
          "Authentication failed for user: " + request.getEmail());
    }

    long expirationTime = 365 * 24 * 60 * 60 * 1000; // 1 year in milliseconds
    List<String> rolesList =
        user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
    String token =
        Jwts.builder()
            .setSubject(user.getEmail())
            .claim("roles", rolesList)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();

    return token;
  }
}
