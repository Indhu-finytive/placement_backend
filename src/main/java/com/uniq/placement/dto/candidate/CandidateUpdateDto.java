package com.uniq.placement.dto.candidate;

import com.uniq.placement.entity.enums.CandidateStatus;
import com.uniq.placement.entity.enums.Eligibility;
import com.uniq.placement.entity.enums.Gender;
import com.uniq.placement.entity.enums.TrainingMode;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CandidateUpdateDto {
    private String candidateName;

    @Pattern(regexp = "^\\d{10}$", message = "Mobile must be 10 digits")
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
    private TrainingMode batchType;
    private String trainer;
    private UUID assignedTeamId;
    private UUID batchId;
    private CandidateStatus status;
    private Eligibility eligibility;
    private String remarks;
}
