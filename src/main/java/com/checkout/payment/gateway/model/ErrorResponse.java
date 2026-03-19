package com.checkout.payment.gateway.model;

import java.util.List;

public class ErrorResponse {

  private final String message;
  private final String status;
  private final List<String> errors;

  public ErrorResponse(String message) {
    this.message = message;
    this.status = null;
    this.errors = null;
  }

  public ErrorResponse(String status, List<String> errors) {
    this.status = status;
    this.errors = errors;
    this.message = null;
  }
  public String getStatus() {
    return status;
  }

  public List<String> getErrors() {
    return errors;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return "ErrorResponse{" +
        "message='" + message + '\'' +
        '}';
  }
}
