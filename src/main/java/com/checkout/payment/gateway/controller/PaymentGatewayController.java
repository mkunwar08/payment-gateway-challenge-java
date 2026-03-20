package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.model.ErrorResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.checkout.payment.gateway.validation.PostPaymentRequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("api")
public class PaymentGatewayController {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayController.class);

  private final PaymentGatewayService paymentGatewayService;
  private final PostPaymentRequestValidator requestValidator;

  public PaymentGatewayController(PaymentGatewayService paymentGatewayService,
      PostPaymentRequestValidator requestValidator) {
    this.paymentGatewayService = paymentGatewayService;
    this.requestValidator = requestValidator;
  }

  @GetMapping("/payment/{id}")
  public ResponseEntity<PostPaymentResponse> getPostPaymentEventById(@PathVariable UUID id) {
    LOG.info("Retrieve payment request for id={}", id);
    PostPaymentResponse response = paymentGatewayService.getPaymentById(id);

    LOG.info("Retrieved payment request for id={} status={}", response.getId(), response.getStatus());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/payments")
  public ResponseEntity<?> processPayment(@RequestBody PostPaymentRequest paymentRequest) {
    LOG.info("Payment request received currency = {} amount = {}", paymentRequest.getCurrency(), paymentRequest.getAmount());

    List<String> errors = requestValidator.validateRequest(paymentRequest);
    if(!errors.isEmpty()) {
      LOG.warn("Payment request rejected due to errors={}", errors);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Rejected", errors));
    }

    PostPaymentResponse response = paymentGatewayService.processPayment(paymentRequest);
    LOG.info("Payment request completed id={} status={}", response.getId(), response.getStatus());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
