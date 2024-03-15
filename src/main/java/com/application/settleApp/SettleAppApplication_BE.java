package com.application.settleApp;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class SettleAppApplication_BE {

  @Autowired private Environment environment;
  private static final Logger logger = LoggerFactory.getLogger(SettleAppApplication_BE.class);

  public static void main(String[] args) {
    SpringApplication.run(SettleAppApplication_BE.class, args);
  }

  @PostConstruct
  public void logActiveProfiles() {
    String[] activeProfiles = environment.getActiveProfiles();
    logger.info("\u001B[32mSettleAppApplication_BE bean created, launching...\u001B[0m");
    logger.info(
        "\u001B[33mThe following {} profile(s) are active: {}\u001B[0m",
        activeProfiles.length,
        String.join(", ", activeProfiles));
  }
}
