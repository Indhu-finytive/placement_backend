package com.uniq.placement.dto.allocation;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ShareAllocationResponseDto {
    private UUID id;
    private String partner;
    private BigDecimal appliedPercent;
    private BigDecimal amount;
    private LocalDate allocationDate;
    private String remarks;
    private UUID allocatedByUserId;
    private Instant allocatedAt;
}
