package com.uniq.placement.dto.candidate;

import lombok.Data;

import java.util.UUID;

@Data
public class CandidateSummaryDto {
    private UUID id;
    private String candidateName;
    private String candidateCode;
    private String mobileNumber;
    private UUID assignedTeamId;
    private String teamName;
    private String team;
}
