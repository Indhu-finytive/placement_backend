package com.uniq.placement.dto.candidate;

import com.uniq.placement.entity.enums.CandidateStatus;
import com.uniq.placement.entity.enums.Eligibility;
import com.uniq.placement.entity.enums.Gender;
import com.uniq.placement.entity.enums.TrainingMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CandidateCreateDto {
    @NotBlank(message = "Name is required")
    private String candidateName;

    @NotBlank(message = "Mobile is required")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile must be 10 digits")
    private String mobileNumber;

    private String alternateMobile;
    private String email;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    private Gender gender;
    private String qualification;
    private String degree;
    private String department;
    private Short passingYear;
    private String collegeName;
    private String currentLocation;

    @NotBlank(message = "Course is required")
    private String course;

    private TrainingMode batchType;
    private String trainer;

    @NotNull(message = "Assigned team is required")
    private UUID assignedTeamId;

    @NotNull(message = "Batch is required")
    private UUID batchId;

    private CandidateStatus status;
    private Eligibility eligibility;
    private String remarks;

    private InitialDocumentFeeDto initialDocumentFee;
}
