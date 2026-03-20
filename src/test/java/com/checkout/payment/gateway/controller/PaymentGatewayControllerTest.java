package com.checkout.payment.gateway.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  PaymentsRepository paymentsRepository;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    PostPaymentResponse payment = new PostPaymentResponse();
    payment.setId(UUID.randomUUID());
    payment.setAmount(10);
    payment.setCurrency("USD");
    payment.setStatus(PaymentStatus.AUTHORIZED);
    payment.setExpiryMonth(12);
    payment.setExpiryYear(2024);
    payment.setCardNumberLastFour(4321);

    paymentsRepository.add(payment);

    mvc.perform(MockMvcRequestBuilders.get("/api/v1/payment/" + payment.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(payment.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(payment.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(payment.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.getCurrency()))
        .andExpect(jsonPath("$.amount").value(payment.getAmount()));
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/api/v1/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Page not found"));
  }
//
//  @Test
////  void whenPaymentIsDeclinedThenItIsStillReturned() throws Exception {
////    Map<String, Object> request = validRequestBody();
////    request.put("card_number", "1234567890987654"); //cardNo ending in even number returns Declined
////
////    mvc.perform(MockMvcRequestBuilders.post("/api/v1/payments")
////            .contentType(MediaType.APPLICATION_JSON)
////            .content(objectMapper.writeValueAsString(request)))
////            .andExpect(jsonPath("$.status").value("Declined"))
////            .andExpect(jsonPath("$.cardNumberLastFour").value(request.get("card_number")
////            .andExpect(jsonPath("$.expiryMonth").value(payment.getExpiryMonth()))
////            .andExpect(jsonPath("$.expiryYear").value(payment.getExpiryYear()))
////            .andExpect(jsonPath("$.currency").value(payment.getCurrency()))
////            .andExpect(jsonPath("$.amount").value(payment.getAmount()));
////  }

  @Test
  void whenNoRequestBodyThenBadRequestIsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post("/api/v1/payments")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }


  @Test
  void whenExpiredCardThenRejectedResponseIsReturned() throws Exception {
    Map<String, Object> request = validRequestBody();
    request.put("expiry_month", 12);
    request.put("expiry_year", 2025);

    mvc.perform(MockMvcRequestBuilders.post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("Rejected"));
  }

  @Test
  void whenInvalidCVVThenRejectedResponseIsReturned() throws Exception {
    Map<String, Object> request = validRequestBody();
    request.put("cvv", "12");

    mvc.perform(MockMvcRequestBuilders.post("/api/v1/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value("Rejected"));
  }

  @Test
  void whenInvalidAmountThenRejectedResponseIsReturned() throws Exception {
    Map<String, Object> request = validRequestBody();
    request.put("amount", -50);

    mvc.perform(MockMvcRequestBuilders.post("/api/v1/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value("Rejected"));
  }

  private Map<String, Object> validRequestBody() {
    Map<String, Object> request = new HashMap<>();
    request.put("card_number", "123456789098765");
    request.put("expiry_month", 12);
    request.put("expiry_year", 2027);
    request.put("currency", "GBP");
    request.put("amount", 100);
    request.put("cvv", "123");
    return request;
  }
}
