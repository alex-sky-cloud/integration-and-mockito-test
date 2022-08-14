package com.example.springbootsliceannotations.jsondeserialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentResponseTest {

    @Autowired
    private JacksonTester<PaymentResponse> jacksonTester;

    private JsonContent<PaymentResponse> result;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void init(){
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setId("42");
        paymentResponse.setAmount(new BigDecimal("42.50"));
        paymentResponse.setPaymentConfirmationCode(UUID.randomUUID());
        paymentResponse.setPaymentTime(LocalDateTime.parse("2020-07-20T19:00:00.123"));

        try {
            result = jacksonTester.write(paymentResponse);
        } catch (IOException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    @Test
    void id() {
        assertNotNull(objectMapper);

        assertThat(result).doesNotHaveJsonPath("$.id");
    }

    @Test
    void paymentConfirmationCode() {
        assertNotNull(objectMapper);

        assertThat(result)
                .hasJsonPathStringValue("$.paymentConfirmationCode");
    }

    @Test
    void amount() {
        assertNotNull(objectMapper);

        assertThat(result)
                .extractingJsonPathNumberValue("$.payment_amount").isEqualTo(42.50);
    }

    @Test
    void paymentTime() {
        assertNotNull(objectMapper);

        assertThat(result)
                .extractingJsonPathStringValue("$.paymentTime").isEqualTo("2020-07-20|19:00:00");
    }
}