package com.uniq.placement.dto.settlement;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PartnerBalanceDto {
    private String partner;
    private BigDecimal earned;
    private BigDecimal receivedDirectly;
    private BigDecimal paid;
    private BigDecimal returned;
    private BigDecimal closingBalance;
}
