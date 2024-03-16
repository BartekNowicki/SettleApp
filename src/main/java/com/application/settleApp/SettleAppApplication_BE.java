package com.application.settleApp;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootApplication
public class SettleAppApplication_BE {

  @Autowired private Environment environment;
  public static void main(String[] args) {
    SpringApplication.run(SettleAppApplication_BE.class, args);
  }

  @PostConstruct
  public void logActiveProfiles() {
    String[] activeProfiles = environment.getActiveProfiles();
    log.info("\u001B[32mSettleAppApplication_BE bean created, launching...\u001B[0m");
    log.info(
        "\u001B[33mThe following {} profile(s) are active: {}\u001B[0m",
        activeProfiles.length,
        String.join(", ", activeProfiles));
  }
}
