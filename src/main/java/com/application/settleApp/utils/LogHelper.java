package com.application.settleApp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogHelper {

  private static final Logger log = LoggerFactory.getLogger(LogHelper.class);

  public static void logThis(String message, String color) {
    String colorCode;

    switch (color.toLowerCase()) {
      case "green":
        colorCode = "\u001B[32m";
        break;
      case "red":
        colorCode = "\u001B[31m";
        break;
      case "yellow":
        colorCode = "\u001B[33m";
        break;
      case "blue":
        colorCode = "\u001B[34m";
        break;
      default:
        colorCode = "\u001B[0m";
    }

    log.info(colorCode + message + "\u001B[0m");
  }
}
