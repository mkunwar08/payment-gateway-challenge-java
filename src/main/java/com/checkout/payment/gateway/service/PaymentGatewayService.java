package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.BankException;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.BankResponse;
import com.checkout.payment.gateway.model.PostBankRequest;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final RestTemplate restTemplate;
  private final String bankURL;

  public PaymentGatewayService(PaymentsRepository paymentsRepository, RestTemplate restTemplate,
      @Value("${bank.url}") String bankURL) {
    this.paymentsRepository = paymentsRepository;
    this.restTemplate = restTemplate;
    System.out.println(bankURL);
    this.bankURL = bankURL;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    PostPaymentResponse payment = paymentsRepository.get(id).orElseThrow(() -> {
      LOG.warn("Payment with id={} not found", id);
      return new EventProcessingException("Invalid ID");
    });
    LOG.info("Payment with id={} and status={} retrieved", payment.getId(), payment.getStatus());
    return payment;
  }

  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    PostBankRequest bankRequest = new PostBankRequest(paymentRequest);
    BankResponse bankResponse = callBank(bankRequest);
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

    LOG.info("Payment processed id={} status={}");
    paymentsRepository.add(paymentResponse);
    return paymentResponse;
  }

  public BankResponse callBank(PostBankRequest bankRequest) {
    LOG.info("Calling Bank at url={}", bankURL);
    try {
      BankResponse response = restTemplate.postForObject(bankURL+ "/payments", bankRequest, BankResponse.class);
      LOG.info("The payment has been authorized={}", response.isAuthorized());
      return response;
    } catch (ResourceAccessException ex) {
      LOG.error("Bank is not available, message={}", ex.getMessage());
      throw new BankException("Bank is not currently available");

    } catch (HttpClientErrorException ex) {
      LOG.error("Bank returned a client error, status={}", ex.getStatusCode());
      throw new BankException("Bank rejected the request");

    } catch (HttpServerErrorException ex) {
      LOG.error("Bank returned a server error, status={}", ex.getStatusCode());
      throw new BankException("Bank returned a server error");

    } catch (Exception ex) {
      LOG.error("Unexpected error calling the bank, message = {}", ex.getMessage());
      throw new BankException("Error forwarding request to bank");
    }
  }
}
