package com.uniq.placement.dto.settlement;

import com.uniq.placement.entity.enums.SettlementDirection;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class SettlementResponseDto {
    private UUID id;
    private String partner;
    private BigDecimal amount;
    private LocalDate settlementDate;
    private String account;
    private SettlementDirection direction;
    private String remarks;
    private UUID enteredByUserId;
    private Instant createdAt;
}
