package com.uniq.placement.dto.payment;

import com.uniq.placement.dto.allocation.ShareAllocationResponseDto;
import com.uniq.placement.entity.enums.PaymentMode;
import com.uniq.placement.entity.enums.PaymentType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PaymentResponseDto {
    private UUID id;
    private String paymentCode;
    private PaymentType paymentType;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private PaymentMode paymentMode;
    private String accountName;
    private String accountHolderName;
    private String referenceNumber;
    private String remarks;
    private UUID receivedById;
    private String receivedByName;
    private UUID teamId;
    private String teamName;
    private ShareAllocationResponseDto allocation;
}
