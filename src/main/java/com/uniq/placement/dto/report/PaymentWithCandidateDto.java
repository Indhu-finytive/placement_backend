package com.uniq.placement.dto.report;

import com.uniq.placement.dto.payment.PaymentResponseDto;
import com.uniq.placement.dto.candidate.CandidateSummaryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentWithCandidateDto extends PaymentResponseDto {
    private CandidateSummaryDto candidate;
}
