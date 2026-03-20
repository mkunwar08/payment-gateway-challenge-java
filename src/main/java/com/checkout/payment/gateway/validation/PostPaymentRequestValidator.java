package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class PostPaymentRequestValidator {

  private static final Set<String> SUPPORTED_CURRENCIES = Set.of("USD", "GBP", "EUR");

  public List<String> validateRequest(PostPaymentRequest request) {
    List<String> errors = new ArrayList<>();
    errors.addAll(validateCardNumber(request.getCardNumber()));
    errors.addAll(validateExpiryMonth(request.getExpiryMonth()));
    errors.addAll(validateExpiryYear(request.getExpiryYear()));
    errors.addAll(validateExpiryDate(request.getExpiryMonth(), request.getExpiryYear()));
    errors.addAll(validateCVV(request.getCvv()));
    errors.addAll(validateCurrency(request.getCurrency()));
    errors.addAll(validateAmount(request.getAmount()));

    return errors;
  }

  public boolean isValid(PostPaymentRequest request) {
    return validateRequest(request).isEmpty();
  }

  public List<String> validateCardNumber(String cardNumber) {
    List<String> errors = new ArrayList<>();

    if(cardNumber == null || cardNumber.isBlank()) {
      errors.add("Card Number must be supplied");
    } else if(cardNumber.length() < 14 || cardNumber.length() > 19) {
      errors.add("Card Number must be between 14 and 19 digits long");
    } else if(!cardNumber.matches("\\d+")){
      errors.add("Card Number must only contain digits");
    }

    return errors;
  }

  public List<String> validateExpiryMonth(Integer expiryMonth) {
    List<String> errors = new ArrayList<>();

    if(expiryMonth == null) {
      errors.add("Expiry Month must be supplied");
    } else if(expiryMonth < 1 || expiryMonth > 12) {
      errors.add("Expiry Month must be between 1 and 12");
    }

    return errors;
  }

  public List<String> validateExpiryYear(Integer expiryYear) {
    List<String> errors = new ArrayList<>();

    if(expiryYear == null) {
      errors.add("Expiry Year must be supplied");
    }

    return errors;
  }

  public List<String> validateExpiryDate(Integer expiryMonth, Integer expiryYear) {
    List<String> errors = new ArrayList<>();
    LocalDateTime currentDate = LocalDateTime.now();
    int currentYear = currentDate.getYear();
    int currentMonth = currentDate.getMonthValue();

    if(!(currentYear <= expiryYear && currentMonth <= expiryMonth)) {
      errors.add("Expiry date must be in the future");
    }

    return errors;
  }

  public List<String> validateCVV(String cvv) {
    List<String> errors = new ArrayList<>();

    if(cvv == null || cvv.isBlank()) {
      errors.add("CVV must be supplied");
    } else if(!cvv.matches("\\d{3,4}")) {
      errors.add("CVV must be 3-4 digits long");
    }

    return errors;
  }

  public List<String> validateCurrency(String currency) {
    List<String> errors = new ArrayList<>();

    if(currency == null || currency.isBlank()) {
      errors.add("Currency must be provided");
    } else if(!SUPPORTED_CURRENCIES.contains(currency.toUpperCase())) {
      errors.add("Currency not supported");
    }

    return errors;
  }

  public List<String> validateAmount(Integer amount) {
    List<String> errors = new ArrayList<>();

    if(amount == null) {
      errors.add("Amount must be provided");
    } else if(amount < 0) {
      errors.add("Amount must be positive");
    }

    return errors;
  }


}
