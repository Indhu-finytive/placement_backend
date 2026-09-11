package com.uniq.placement.dto.report;

import com.uniq.placement.dto.account.PaymentAccountSummaryDto;
import com.uniq.placement.dto.settlement.PartnerBalanceDto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ReportOverviewDto {
    private BigDecimal totalCollection;
    private BigDecimal documentFees;
    private BigDecimal placementCollection;
    private BigDecimal cashCollection;
    private BigDecimal candidateOutstanding;
    private BigDecimal overdue;
    private BigDecimal partnerPayable;
    private BigDecimal partnerReceivable;
    
    private List<MonthlyTrendDto> monthlyTrend;
    private List<PaymentAccountSummaryDto> accountUsage;
    private List<PartnerBalanceDto> partnerBalances;
}
