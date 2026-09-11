package com.uniq.placement.dto.report;

import com.uniq.placement.dto.candidate.CandidateSummaryDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OutstandingRowDto {
    private CandidateSummaryDto candidate;
    private String company;
    private LocalDate placementDate;
    private LocalDate dueDate;
    private BigDecimal ctc;
    private BigDecimal commitmentAmount;
    private BigDecimal collected;
    private BigDecimal outstanding;
    private Integer ageingDays;
    private Integer overdueDays;
}
