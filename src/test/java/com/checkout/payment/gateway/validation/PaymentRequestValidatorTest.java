package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentRequestValidatorTest {

  private PostPaymentRequestValidator validator;

  @BeforeEach
  void setUp() {
    validator = new PostPaymentRequestValidator();
  }

  //set up a valid request first
  private PostPaymentRequest getValidRequest() {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("123456789098765");
    request.setExpiryMonth(10);
    request.setExpiryYear(2026);
    request.setCurrency("GBP");
    request.setAmount(50);
    request.setCvv("1234");
    return request;
  }

  @Test
  void validRequestPassesValidation() {
    assertTrue(validator.isValid(getValidRequest()));
  }

  @Test
  void lessThanFourteenDigitsCardNoFailsValidation() {
    PostPaymentRequest request = getValidRequest();
    String cardNo = ("123456789012");
    request.setCardNumber(cardNo);
    assertFalse(validator.validateCardNumber(cardNo).isEmpty());
    assertFalse(validator.isValid(request));
  }

  @Test
  void moreThanNineteenDigitsCardNoFailsValidation() {
    PostPaymentRequest request = getValidRequest();
    String cardNo = ("1234567890121234567890");
    request.setCardNumber(cardNo);
    assertFalse(validator.validateCardNumber(cardNo).isEmpty());
    assertFalse(validator.isValid(request));
  }

  @Test
  void cardNoContainingLettersFailsValidation() {
    PostPaymentRequest request = getValidRequest();
    String cardNo = ("12345678901234CT");
    request.setCardNumber(cardNo);
    assertFalse(validator.validateCardNumber(cardNo).isEmpty());
    assertFalse(validator.isValid(request));
  }

  @Test
  void cardNoContainingSpacesFailsValidation() {
    PostPaymentRequest request = getValidRequest();
    String cardNo = ("1234567890 1234C");
    request.setCardNumber(cardNo);
    assertFalse(validator.validateCardNumber(cardNo).isEmpty());
    assertFalse(validator.isValid(request));
  }
}
