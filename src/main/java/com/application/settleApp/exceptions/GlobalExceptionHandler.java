package com.application.settleApp.exceptions;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    ApiError apiError =
        new ApiError(HttpStatus.BAD_REQUEST, "http message not readable: " + ex.getMessage());
    log.info("\u001B" + "[32m" + apiError.getMessage() + "\u001B[0m");
    return new ResponseEntity<>(apiError, status);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    ApiError apiError =
        new ApiError(HttpStatus.BAD_REQUEST, "invalid method argument: " + ex.getMessage());
    log.info("\u001B" + "[32m" + apiError.getMessage() + "\u001B[0m");
    return new ResponseEntity<>(apiError, status);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
    log.info("\u001B" + "[32m" + apiError.getMessage() + "\u001B[0m");
    return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
    log.info("\u001B" + "[32m" + apiError.getMessage() + "\u001B[0m");
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AuthenticationFailedException.class)
  public ResponseEntity<Object> handleAuthenticationFailed(AuthenticationFailedException ex) {
    ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getMessage());
    logger.info("\u001B" + "[32m" + apiError.getMessage() + "\u001B[0m");
    return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(RegistrationFailedException.class)
  public ResponseEntity<Object> handleRegistrationFailed(RegistrationFailedException ex) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
    log.info("\u001B" + "[32m" + apiError.getMessage() + "\u001B[0m");
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }
}
