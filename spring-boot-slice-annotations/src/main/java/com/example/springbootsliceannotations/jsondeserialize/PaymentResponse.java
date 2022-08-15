package com.example.springbootsliceannotations.jsondeserialize;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PaymentResponse {

  @JsonIgnore
  private String id;

  private UUID paymentConfirmationCode;

  @JsonProperty("payment_amount")
  private BigDecimal amount;

  @JsonFormat(
    shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd|HH:mm:ss", locale = "en_US")
  private LocalDateTime paymentTime;
}