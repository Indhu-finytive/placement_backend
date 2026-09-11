package com.uniq.placement.dto.account;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentAccountSummaryDto {
    private String name;
    private Long transactionCount;
    private BigDecimal totalReceived;
    private BigDecimal percentageOfCollection;
    private LocalDate lastTransactionDate;
}
