package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  @ExceptionHandler(EventProcessingException.class)
  public ResponseEntity<ErrorResponse> handleEventProcessingException(EventProcessingException ex) {
    LOG.error("Payment not found message={}", ex.getMessage());
    return new ResponseEntity<>(new ErrorResponse("Page not found"),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BankException.class)
  public ResponseEntity<ErrorResponse> handleBankException(BankException ex) {
    LOG.error("Bank exception happened", ex);
    return new ResponseEntity<>(new ErrorResponse("Payment could not be processed"), HttpStatus.SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(BankException ex) {
    LOG.error("Unexpected error occured {}", ex.getMessage());
    return new ResponseEntity<>(new ErrorResponse("An unexpected error occured"), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
