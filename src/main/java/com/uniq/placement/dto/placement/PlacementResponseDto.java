package com.uniq.placement.dto.placement;

import com.uniq.placement.entity.enums.DuePeriod;
import com.uniq.placement.entity.enums.PlacementStatusEnum;
import com.uniq.placement.entity.enums.ReferralType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PlacementResponseDto {
    private UUID id;
    private String companyName;
    private String jobRole;
    private String companyLocation;
    private LocalDate placementDate;
    private LocalDate joiningDate;
    private Integer duePeriodDays;
    private DuePeriod duePeriodLabel;
    private LocalDate autoCalculatedDueDate;
    private LocalDate finalAppliedDueDate;
    private BigDecimal annualCtc;
    private BigDecimal committedPercentage;
    private BigDecimal committedAmount;
    private String paymentTerms;
    private ReferralType referralType;
    private UUID shareTeamId;
    private PlacementStatusEnum status;
    private String offerLetterUrl;
    private String remarks;
}
