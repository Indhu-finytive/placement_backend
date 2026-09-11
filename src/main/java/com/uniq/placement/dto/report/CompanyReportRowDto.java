package com.uniq.placement.dto.report;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CompanyReportRowDto {
    private String company;
    private Long candidates;
    private BigDecimal totalCtc;
    private BigDecimal totalCommitment;
    private BigDecimal totalCollected;
    private BigDecimal outstanding;
}
