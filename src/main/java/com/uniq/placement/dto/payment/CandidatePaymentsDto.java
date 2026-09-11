package com.uniq.placement.dto.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CandidatePaymentsDto {
    private List<PaymentResponseDto> items;
    private BigDecimal totalCollected;
    private BigDecimal totalShareAllocated;
    private BigDecimal candidateOutstanding;
}
