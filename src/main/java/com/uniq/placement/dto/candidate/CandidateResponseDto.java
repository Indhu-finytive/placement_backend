package com.uniq.placement.dto.candidate;

import com.uniq.placement.dto.payment.PaymentResponseDto;
import com.uniq.placement.dto.placement.PlacementResponseDto;
import com.uniq.placement.entity.enums.CandidateStatus;
import com.uniq.placement.entity.enums.Eligibility;
import com.uniq.placement.entity.enums.Gender;
import com.uniq.placement.entity.enums.TrainingMode;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class CandidateResponseDto {
    private UUID id;
    private String candidateCode;
    private String candidateName;
    private String mobileNumber;
    private String alternateMobile;
    private String email;
    private LocalDate joiningDate;
    private Gender gender;
    private String qualification;
    private String degree;
    private String department;
    private Short passingYear;
    private String collegeName;
    private String currentLocation;
    private String course;
    private String batch;
    private TrainingMode batchType;
    private String branch;
    private String branchName;
    private String trainer;
    private UUID assignedTeamId;
    private CandidateStatus status;
    private Eligibility eligibility;
    private String remarks;

    private PlacementResponseDto placement;
    private List<PaymentResponseDto> payments;
    private List<String> activity; // Inferred from audit logs

    private BigDecimal totalCollected;
    private BigDecimal outstanding;
}
