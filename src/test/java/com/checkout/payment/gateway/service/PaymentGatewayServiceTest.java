package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.BankResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class PaymentGatewayServiceTest {

  private PaymentGatewayService service;
  private RestTemplate restTemplate;
  private PaymentsRepository paymentsRepository;

  @BeforeEach
  void setUp() {
    restTemplate = Mockito.mock(RestTemplate.class);
    paymentsRepository = new PaymentsRepository();
    service = new PaymentGatewayService(paymentsRepository, restTemplate, "http://localhost:8080");
  }

  private PostPaymentRequest validRequest() {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("12345678909876");
    request.setExpiryMonth(12);
    request.setExpiryYear(2027);
    request.setCurrency("EUR");
    request.setAmount(100);
    request.setCvv("123");
    return request;
  }

  @Test
  void whenBankApprovesPaymentAllFieldsAreReturned() {
    BankResponse bankResponse = new BankResponse();
    bankResponse.setAuthorized(true);
    when(restTemplate.postForObject(anyString(), any(), eq(BankResponse.class)))
        .thenReturn(bankResponse);

    PostPaymentResponse response = service.processPayment(validRequest());

    assertEquals(PaymentStatus.AUTHORIZED, response.getStatus());
    assertNotNull(response.getId());
    assertEquals(9876, response.getCardNumberLastFour());
    assertEquals("EUR", response.getCurrency());
    assertEquals(100, response.getAmount());
    assertEquals(12, response.getExpiryMonth());
    assertEquals(2027, response.getExpiryYear());
  }

  @Test
  void whenBankDeclinesPaymentStatusIsReturned() {
    BankResponse bankResponse = new BankResponse();
    bankResponse.setAuthorized(false);
    when(restTemplate.postForObject(anyString(), any(), eq(BankResponse.class)))
        .thenReturn(bankResponse);

    PostPaymentResponse response = service.processPayment(validRequest());

    assertEquals(PaymentStatus.DECLINED, response.getStatus());
  }

  @Test
  void whenPaymentIsProcessedThenSavedToRepository() {
    BankResponse bankResponse = new BankResponse();
    bankResponse.setAuthorized(true);
    when(restTemplate.postForObject(anyString(), any(), eq(BankResponse.class)))
        .thenReturn(bankResponse);

    PostPaymentResponse response = service.processPayment(validRequest());
    PostPaymentResponse retrieved = service.getPaymentById(response.getId());

    assertEquals(response.getId(), retrieved.getId());
    assertEquals(PaymentStatus.AUTHORIZED, retrieved.getStatus());
  }

  @Test
  void whenPaymentIsProcessedAndDeclinedThenSavedToRepository() {
    BankResponse bankResponse = new BankResponse();
    bankResponse.setAuthorized(false);
    when(restTemplate.postForObject(anyString(), any(), eq(BankResponse.class)))
        .thenReturn(bankResponse);

    PostPaymentResponse response = service.processPayment(validRequest());
    PostPaymentResponse retrieved = service.getPaymentById(response.getId());

    assertEquals(response.getId(), retrieved.getId());
    assertEquals(PaymentStatus.DECLINED, retrieved.getStatus());
  }

  @Test
  void whenPaymentIDDoesNotExistThenThrowException() {
    assertThrows(EventProcessingException.class, () -> service.getPaymentById(UUID.randomUUID()));
  }

  @Test
  void whenPaymentIDExistsThenPaymentIsReturned() {
    BankResponse bankResponse = new BankResponse();
    bankResponse.setAuthorized(true);
    when(restTemplate.postForObject(anyString(), any(), eq(BankResponse.class)))
        .thenReturn(bankResponse);

    PostPaymentResponse savedResponse = service.processPayment(validRequest());
    PostPaymentResponse retrievedResponse = service.getPaymentById(savedResponse.getId());

    assertEquals(savedResponse.getId(), retrievedResponse.getId());
    assertEquals(PaymentStatus.AUTHORIZED, retrievedResponse.getStatus());
  }

}
