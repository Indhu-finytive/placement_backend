package com.uniq.placement.dto.payment;

import com.uniq.placement.entity.enums.PaymentMode;
import com.uniq.placement.entity.enums.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PaymentCreateDto {
    @NotNull(message = "Date is required")
    private LocalDate paymentDate;

    @NotNull(message = "Type is required")
    private PaymentType paymentType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Mode is required")
    private PaymentMode paymentMode;

    private String accountName;

    @NotNull(message = "Account holder is required")
    private UUID accountHolderId;

    private String referenceNumber;
    private String remarks;
}
