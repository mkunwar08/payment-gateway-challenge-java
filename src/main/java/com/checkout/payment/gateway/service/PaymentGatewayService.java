package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.BankResponse;
import com.checkout.payment.gateway.model.PostBankRequest;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final RestTemplate restTemplate;

  public PaymentGatewayService(PaymentsRepository paymentsRepository, RestTemplate restTemplate) {
    this.paymentsRepository = paymentsRepository;
    this.restTemplate = restTemplate;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    PostBankRequest bankRequest = new PostBankRequest(paymentRequest);
    System.out.println(paymentRequest.toString());
    BankResponse bankResponse = restTemplate.postForObject("http://localhost:8080/payments", paymentRequest, BankResponse.class);

    PostPaymentResponse paymentResponse = new PostPaymentResponse();
    int cardLastFour = Integer.parseInt(
        paymentRequest.getCardNumber().substring(paymentRequest.getCardNumber().length() -4));
    paymentResponse.setId(UUID.randomUUID());
    paymentResponse.setCardNumberLastFour(cardLastFour);
    paymentResponse.setExpiryMonth(paymentRequest.getExpiryMonth());
    paymentResponse.setExpiryYear(paymentRequest.getExpiryYear());
    paymentResponse.setCurrency(paymentRequest.getCurrency());
    paymentResponse.setAmount(paymentRequest.getAmount());
    paymentResponse.setStatus(bankResponse.isAuthorized() ? PaymentStatus.AUTHORIZED: PaymentStatus.DECLINED);

    paymentsRepository.add(paymentResponse);
    return paymentResponse;
  }
}
