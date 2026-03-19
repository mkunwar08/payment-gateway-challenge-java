package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostBankRequest {

  @JsonProperty("card_number")
  private String cardNumber;
//  @JsonProperty("expiry_Month")
//  private String expiryMonth;
//  @JsonProperty("expiry_Year")
//  private String expiryYear;
  @JsonProperty("expiry_date")
  private String expiryDate;
  private String currency;
  private int amount;
  private String cvv;

  public PostBankRequest(PostPaymentRequest request) {
    this.cardNumber = request.getCardNumber();
    this.currency = request.getCurrency();
    this.amount = request.getAmount();
    this.cvv = String.valueOf(request.getCvv());
//    this.expiryMonth = String.valueOf(request.getExpiryMonth());
//    this.expiryYear = String.valueOf(request.getExpiryYear());
    this.expiryDate = String.valueOf(request.getExpiryMonth()) + "/" + String.valueOf(request.getExpiryYear());
  }

  public String getCardNumber() {
    return cardNumber;
  }

//  public String getExpiryMonth() {
//    return expiryMonth;
//  }
//
//  public String getExpiryYear() {
//    return expiryYear;
//  }

  public String getExpiryDate() {
    return expiryDate;
  }

  public String getCurrency() {
    return currency;
  }

  public int getAmount() {
    return amount;
  }

  public String getCvv() {
    return cvv;
  }

  @Override
  public String toString() {
    return "PostBankRequest{" +
        "cardNumber='" + cardNumber + '\'' +
//        ", expiryMonth='" + expiryMonth + '\'' +
//        ", expiryYear='" + expiryYear + '\'' +
        ", expiryDate='" + expiryDate + '\'' +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv='" + cvv + '\'' +
        '}';
  }
}
