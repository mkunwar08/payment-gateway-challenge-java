package com.checkout.payment.gateway.model;

public class PostBankRequest {

  private String cardNumber;
  private String expiryMonth;
  private String expiryYear;
  private String currency;
  private String amount;
  private String cvv;

  public PostBankRequest(PostPaymentRequest request) {
    this.cardNumber = request.getCardNumber();
    this.expiryYear = String.valueOf(request.getExpiryYear());
    this.currency = request.getCardNumber();
    this.expiryMonth = String.valueOf(request.getExpiryMonth());
    this.amount = String.valueOf(request.getAmount());
    this.cvv = String.valueOf(request.getCvv());
  }
}
