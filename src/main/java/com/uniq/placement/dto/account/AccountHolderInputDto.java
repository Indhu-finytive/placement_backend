package com.uniq.placement.dto.account;

import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.entity.enums.LedgerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class AccountHolderInputDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Display name is required")
    private String displayName;

    @NotNull(message = "Linked ledger type is required")
    private LedgerType linkedLedgerType;

    private UUID linkedTeam;

    private String bank;

    @Pattern(regexp = "^\\d{4}$", message = "Last4 must be exactly 4 digits")
    private String last4;

    private String upi;

    private String paymentType;

    @NotNull(message = "Status is required")
    private ActiveStatus status;

    private String remarks;
}
