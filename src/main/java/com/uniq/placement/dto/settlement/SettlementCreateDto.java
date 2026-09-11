package com.uniq.placement.dto.settlement;

import com.uniq.placement.entity.enums.SettlementDirection;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SettlementCreateDto {
    @NotBlank(message = "Partner is required")
    private String partner;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private String account;

    private SettlementDirection direction = SettlementDirection.PAID_TO_PARTNER;

    private String remarks;
}
