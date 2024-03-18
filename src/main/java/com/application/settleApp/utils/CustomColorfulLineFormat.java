package com.application.settleApp.utils;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.springframework.stereotype.Component;

@Component
public class CustomColorfulLineFormat implements MessageFormattingStrategy {

  @Override
  public String formatMessage(
      int connectionId,
      String now,
      long elapsed,
      String category,
      String prepared,
      String sql,
      String url) {
    String greenSql = String.format("\u001B[32m%s\u001B[0m", sql);

    return String.format("[%s] %s: %s", now, category, greenSql);
  }
}
