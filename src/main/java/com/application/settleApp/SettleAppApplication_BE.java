package com.application.settleApp;

import com.application.settleApp.utils.LogHelper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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
    LogHelper.logThis("SettleAppApplication_BE bean created, launching...", "green");
    LogHelper.logThis("The following profiles are active: ", "green");
    LogHelper.logThis(String.join(", ", environment.getActiveProfiles()), "yellow");
  }
}
