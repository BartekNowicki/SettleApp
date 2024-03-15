package com.application.settleApp.services;

import com.application.settleApp.enums.RoleType;
import com.application.settleApp.exceptions.AuthenticationFailedException;
import com.application.settleApp.exceptions.RegistrationFailedException;
import com.application.settleApp.models.Role;
import com.application.settleApp.models.User;
import com.application.settleApp.repositories.RoleRepository;
import com.application.settleApp.repositories.UserRepository;
import com.application.settleApp.security.AuthRequest;
import com.application.settleApp.security.RegistrationRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
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

  public String registerUser(RegistrationRequest request) {
    User existingUser = userRepository.findByEmail(request.getEmail());
    if (existingUser != null) {
      throw new RegistrationFailedException(
          "User with email " + request.getEmail() + " already exists");
    }

    User newUser = new User();
    newUser.setEmail(request.getEmail());
    newUser.setCreationDate(LocalDateTime.now());
    newUser.setModificationDate(LocalDateTime.now());
    newUser.setPassword(passwordEncoder.encode(request.getPassword()));
    Role newUserRole = roleRepository.findByName(RoleType.USER.name());
    newUser.setRoles(Set.of(newUserRole));

    userRepository.save(newUser);

    return generateToken(new AuthRequest(request.getEmail(), request.getPassword()));
  }
}
