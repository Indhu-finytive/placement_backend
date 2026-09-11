package com.uniq.placement.dto.placement;

import com.uniq.placement.entity.enums.DuePeriod;
import com.uniq.placement.entity.enums.ReferralType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PlacementInputDto {
    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Job role is required")
    private String jobRole;

    @NotBlank(message = "Location is required")
    private String companyLocation;

    @NotNull(message = "Placement date is required")
    private LocalDate placementDate;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    @NotNull(message = "CTC is required")
    @DecimalMin(value = "0.01", message = "CTC must be greater than 0")
    private BigDecimal annualCtc;

    @NotNull(message = "Percentage is required")
    @Min(value = 0, message = "Percentage cannot be negative")
    @Max(value = 100, message = "Percentage cannot exceed 100")
    private BigDecimal committedPercentage;

    @NotNull(message = "Due period is required")
    private DuePeriod duePeriod;

    private Integer customDueDays;

    private LocalDate dueDate;

    private String paymentTerms;

    private ReferralType referralType;

    @NotNull(message = "Share team is required")
    private UUID shareTeamId;

    private String remarks;
}
