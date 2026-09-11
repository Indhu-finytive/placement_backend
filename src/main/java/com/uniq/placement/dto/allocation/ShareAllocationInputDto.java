package com.uniq.placement.dto.allocation;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ShareAllocationInputDto {
    @NotBlank(message = "Partner is required")
    private String partner;

    @NotNull(message = "Applied percent is required")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal appliedPercent;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0")
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private String remarks;
}
