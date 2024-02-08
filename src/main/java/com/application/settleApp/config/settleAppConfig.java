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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class settleAppConfig {

  @Autowired private EventRepository eventRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private CostRepository costRepository;

  @Bean
  public EventMapper eventMapper() {
    return new EventMapper();
  }

  @Bean
  public UserServiceImpl userService() {
    return new UserServiceImpl(userRepository);
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
    return new EventServiceImpl(eventRepository, userService(), costService());
  }
}
