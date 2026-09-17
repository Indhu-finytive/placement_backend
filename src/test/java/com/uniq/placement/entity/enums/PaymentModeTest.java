package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.uniq.placement.dto.candidate.InitialDocumentFeeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentModeTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldDeserializePaymentModeFromString() throws Exception {
        assertEquals(PaymentMode.CASH, objectMapper.readValue("\"CASH\"", PaymentMode.class));
        assertEquals(PaymentMode.CASH, objectMapper.readValue("\"Cash\"", PaymentMode.class));
        assertEquals(PaymentMode.CASH, objectMapper.readValue("\"cash\"", PaymentMode.class));
        assertEquals(PaymentMode.BANK_TRANSFER, objectMapper.readValue("\"BANK_TRANSFER\"", PaymentMode.class));
        assertEquals(PaymentMode.BANK_TRANSFER, objectMapper.readValue("\"Bank Transfer\"", PaymentMode.class));
        assertEquals(PaymentMode.BANK_TRANSFER, objectMapper.readValue("\"bank transfer\"", PaymentMode.class));
        assertEquals(PaymentMode.UPI, objectMapper.readValue("\"UPI\"", PaymentMode.class));
        assertEquals(PaymentMode.QR, objectMapper.readValue("\"QR\"", PaymentMode.class));
        assertEquals(PaymentMode.OTHER, objectMapper.readValue("\"OTHER\"", PaymentMode.class));
    }

    @Test
    void shouldDeserializeInitialDocumentFeeDtoWithCashMode() throws Exception {
        String json = """
                {
                    "amount": 5000.00,
                    "date": "2026-09-17",
                    "mode": "CASH",
                    "remarks": "Document verification fee"
                }
                """;
        InitialDocumentFeeDto dto = objectMapper.readValue(json, InitialDocumentFeeDto.class);
        assertNotNull(dto);
        assertEquals(PaymentMode.CASH, dto.getMode());
    }

    @Test
    void shouldFailWithInformativeMessageOnInvalidPaymentMode() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            PaymentMode.fromValue("INVALID_PAYMENT_MODE");
        });
        assertTrue(ex.getMessage().contains("Invalid PaymentMode"));
        assertTrue(ex.getMessage().contains("CASH"));
    }
}
