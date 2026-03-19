package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import java.util.List;
import java.util.UUID;
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

  private final PaymentGatewayService paymentGatewayService;
  private final PostPaymentRequestValidator requestValidator;

  public PaymentGatewayController(PaymentGatewayService paymentGatewayService,
      PostPaymentRequestValidator requestValidator) {
    this.paymentGatewayService = paymentGatewayService;
    this.requestValidator = requestValidator;
  }

  @GetMapping("/payment/{id}")
  public ResponseEntity<PostPaymentResponse> getPostPaymentEventById(@PathVariable UUID id) {
    return new ResponseEntity<>(paymentGatewayService.getPaymentById(id), HttpStatus.OK);
  }

  @PostMapping("/payments")
  public ResponseEntity<PostPaymentResponse> processPayment(@RequestBody PostPaymentRequest paymentRequest) {

    List<String> errors = requestValidator.validateRequest(paymentRequest);
    if(!errors.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    PostPaymentResponse response = paymentGatewayService.processPayment(paymentRequest);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
