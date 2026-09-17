package com.uniq.placement.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.uniq.placement.dto.candidate.CandidateCreateDto;
import com.uniq.placement.dto.common.ProblemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldHandleHttpMessageNotReadableExceptionWithCleanMessage() {
        String invalidJson = """
                {
                    "candidateName": "John Doe",
                    "mobileNumber": "9876543210",
                    "joiningDate": "2026-09-17",
                    "course": "Java",
                    "batch": "B1",
                    "initialDocumentFee": {
                        "amount": 1000,
                        "date": "2026-09-17",
                        "mode": "INVALID_MODE"
                    }
                }
                """;

        HttpInputMessage inputMessage = new MockHttpInputMessage(new ByteArrayInputStream(invalidJson.getBytes(StandardCharsets.UTF_8)));

        try {
            objectMapper.readValue(invalidJson, CandidateCreateDto.class);
            fail("Expected exception during deserialization");
        } catch (Exception ex) {
            HttpMessageNotReadableException readableEx = new HttpMessageNotReadableException("JSON parse error", ex, inputMessage);
            ResponseEntity<ProblemDto> response = exceptionHandler.handleHttpMessageNotReadableException(readableEx, new MockHttpServletRequest());

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Invalid Request", response.getBody().getTitle());
            assertTrue(response.getBody().getDetail().contains("Invalid PaymentMode") || response.getBody().getDetail().contains("mode"));
            assertFalse(response.getBody().getDetail().contains("StreamReadFeature"));
        }
    }
}
