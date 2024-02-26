package com.application.settleApp.config;

import com.application.settleApp.mappers.CostMapper;
import com.application.settleApp.mappers.EventMapper;
import com.application.settleApp.mappers.UserMapper;
import com.application.settleApp.repositories.CostRepository;
import com.application.settleApp.repositories.EventRepository;
import com.application.settleApp.repositories.UserRepository;
import com.application.settleApp.services.CostServiceImpl;
import com.application.settleApp.services.EventServiceImpl;
import com.application.settleApp.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class settleAppConfig {

  @Autowired private EventRepository eventRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private CostRepository costRepository;

  // Injecting the CORS allowed origins from the application.properties file
  @Value("${cors.allowedOrigins}")
  private String allowedOrigins;

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
}
