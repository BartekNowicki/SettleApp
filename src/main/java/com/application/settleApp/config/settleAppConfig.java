package com.application.settleApp.config;

import com.application.settleApp.mappers.CostMapper;
import com.application.settleApp.mappers.EventMapper;
import com.application.settleApp.mappers.UserMapper;
import com.application.settleApp.repositories.CostRepository;
import com.application.settleApp.repositories.EventRepository;
import com.application.settleApp.repositories.RoleRepository;
import com.application.settleApp.repositories.UserRepository;
import com.application.settleApp.security.JwtTokenFilter;
import com.application.settleApp.services.AuthService;
import com.application.settleApp.services.CostServiceImpl;
import com.application.settleApp.services.EventServiceImpl;
import com.application.settleApp.services.UserServiceImpl;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class settleAppConfig {

  @Autowired private EventRepository eventRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private CostRepository costRepository;
  @Autowired private RoleRepository roleRepository;

  @Value("${jwt.secret}")
  private String secretKey;

  // Injecting the CORS allowed origins from the application.properties file
  @Value("${cors.allowedOrigins}")
  private String allowedOrigins;

  @Bean
  public SecretKey jwtSecretKeyDecoded() {
    byte[] decodedKey = Base64.getDecoder().decode(secretKey.getBytes());
    return Keys.hmacShaKeyFor(decodedKey);
  }

  @Bean
  public EventMapper eventMapper() {
    return new EventMapper();
  }

  @Bean
  public UserServiceImpl userService() {
    return new UserServiceImpl(userRepository, costRepository);
  }

  @Bean
  public UserMapper userMapper() {
    return new UserMapper();
  }

  @Bean
  public CostServiceImpl costService() {
    return new CostServiceImpl(userService(), eventRepository, costRepository);
  }

  @Bean
  public CostMapper costMapper() {
    return new CostMapper();
  }

  @Bean
  public EventServiceImpl eventService() {
    return new EventServiceImpl(eventRepository, userRepository, userService(), costService());
  }

  @Bean
  public AuthService authService(RoleRepository roleRepository) {
    return new AuthService(userRepository, roleRepository, passwordEncoder(), secretKey);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedOrigins(allowedOrigins)
            .allowedMethods("GET", "POST", "PUT", "DELETE");
      }
    };
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            requests ->
                requests
                    .requestMatchers(
                        "/authenticate",
                        "/register",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/api-docs/swagger-config",
                        "/api-docs/swagger-config",
                        "/api-docs/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public JwtTokenFilter jwtTokenFilter() {
    return new JwtTokenFilter();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
