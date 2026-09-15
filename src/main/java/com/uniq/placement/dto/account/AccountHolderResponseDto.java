package com.uniq.placement.dto.account;

import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.entity.enums.LedgerType;
import lombok.Data;

import java.util.UUID;

@Data
public class AccountHolderResponseDto {
    private UUID id;
    private String name;
    private String displayName;
    private LedgerType linkedLedgerType;
    private String linkedTeam;
    private UUID linkedTeamId;
    private String bank;
    private String last4;
    private String upi;
    private String paymentType;
    private ActiveStatus status;
    private String remarks;
}
