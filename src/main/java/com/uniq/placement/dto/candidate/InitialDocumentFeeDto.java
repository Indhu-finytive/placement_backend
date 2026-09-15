package com.uniq.placement.dto.candidate;

import com.uniq.placement.entity.enums.PaymentMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class InitialDocumentFeeDto {
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Mode is required")
    private PaymentMode mode;

    private String account;

    private UUID accountHolderId;

    private String paymentReferenceId;
    private String remarks;
}
