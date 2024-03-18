package com.application.settleApp.enums;

public enum ExceptionMessage {
  HTTP_MESSAGE_NOT_READABLE("http message not readable: "),
  INVALID_METHOD_ARGUMENT("invalid method argument: "),
  EVENT_NOT_FOUND("Event not found with id: "),
  USER_NOT_FOUND("User not found with id: "),
  COST_NOT_FOUND("Cost not found with id: "),
  USER_AUTHENTICATION_FAILED("User authentication failed"),
  USER_REGISTRATION_FAILED("User registration failed"),
  ID_AUTOINCREMENTED("Id is autoincremented and should not be provided"),
  BOTH_USERID_EVENTID_REQUIRED("Both userId and eventId must be provided"),
  MISMATCH_COST_ID("Mismatch between path variable costId and costDTO id"),
  MISMATCH_EVENT_ID("Mismatch between path variable eventId and eventDTO id"),
  MISMATCH_USER_ID("Mismatch between path variable userId and userDTO id"),
  FAILED_TO_CREATE_INSTANCE("Failed to create instance of "),
  CANNOT_DELETE_NULL_COST("Cannot delete a null cost."),
  CANNOT_DELETE_NULL_EVENT("Cannot delete a null event."),
  CANNOT_DELETE_NULL_USER("Cannot delete a null user.");

  private final String message;

  ExceptionMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
