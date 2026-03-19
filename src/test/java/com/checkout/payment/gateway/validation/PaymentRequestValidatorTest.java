package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

  //tests for card number
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

  //tests for expiry dates
  @Test
  void expiryDateInThePastFailsValidation() {
    PostPaymentRequest request = getValidRequest();
    request.setExpiryMonth(12);
    request.setExpiryYear(2025);
    assertFalse(validator.isValid(request));
  }

  //tests for currency
  @ParameterizedTest
  @ValueSource(strings = {"GBP", "EUR", "USD"})
  void supportedCurrencyPassesValidation(String currency) {
    PostPaymentRequest request = getValidRequest();
    request.setCurrency(currency);
    assertTrue(validator.isValid(request));
  }

  @Test
  void unsupportedCurrencyFailsValidation() {
    PostPaymentRequest request = getValidRequest();
    request.setCurrency("YEN");
    assertFalse(validator.isValid(request));
  }

  //tests for cvv
  @ParameterizedTest
  @ValueSource(strings = {"01234", "01", "xyz"})
  public void invalidCvvFailsValidation(String cvv) {
    PostPaymentRequest request = getValidRequest();
    request.setCvv(cvv);
    assertFalse(validator.isValid(request));
  }

}
