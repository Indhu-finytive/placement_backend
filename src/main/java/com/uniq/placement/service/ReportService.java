package com.uniq.placement.service;

import com.uniq.placement.dto.candidate.CandidateSummaryDto;
import com.uniq.placement.dto.report.CompanyReportRowDto;
import com.uniq.placement.dto.report.MonthlyTrendDto;
import com.uniq.placement.dto.report.OutstandingRowDto;
import com.uniq.placement.dto.report.PaymentWithCandidateDto;
import com.uniq.placement.dto.report.ReportOverviewDto;
import com.uniq.placement.entity.Candidate;
import com.uniq.placement.entity.Payment;
import com.uniq.placement.entity.Team;
import com.uniq.placement.entity.User;
import com.uniq.placement.entity.enums.PaymentType;
import com.uniq.placement.entity.enums.UserRole;
import com.uniq.placement.repository.CandidateRepository;
import com.uniq.placement.repository.PaymentRepository;
import com.uniq.placement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final PaymentRepository paymentRepository;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final AccountHolderService accountHolderService;
    private final SettlementService settlementService;
    private final PaymentService paymentService;

    @Transactional(readOnly = true)
    public ReportOverviewDto getOverview(LocalDate from, LocalDate to, String team) {
        ReportOverviewDto dto = new ReportOverviewDto();
        
        dto.setAccountUsage(accountHolderService.getPaymentAccountSummaries());
        dto.setPartnerBalances(settlementService.getPartnerBalances());
        
        // Dummy values for the rest for now
        dto.setTotalCollection(BigDecimal.ZERO);
        dto.setDocumentFees(BigDecimal.ZERO);
        dto.setPlacementCollection(BigDecimal.ZERO);
        dto.setCashCollection(BigDecimal.ZERO);
        dto.setCandidateOutstanding(BigDecimal.ZERO);
        dto.setOverdue(BigDecimal.ZERO);
        dto.setPartnerPayable(BigDecimal.ZERO);
        dto.setPartnerReceivable(BigDecimal.ZERO);
        dto.setMonthlyTrend(new ArrayList<>());
        
        return dto;
    }

    @Transactional(readOnly = true)
    public List<PaymentWithCandidateDto> getCollections(LocalDate from, LocalDate to, String team, UUID userId, String account, PaymentType type, String search) {
        User currentUser = getCurrentUser();
        Collection<UUID> teamIds = currentUser.getRole() == UserRole.ADMIN ? null : 
            currentUser.getTeams().stream().map(Team::getId).collect(Collectors.toList());
            
        if (team != null && !team.isBlank()) {
            teamIds = List.of(UUID.fromString(team));
        }

        List<Payment> payments = paymentRepository.findCollections(from, to, teamIds, userId, account, type, search);

        return payments.stream().map(p -> {
            PaymentWithCandidateDto dto = new PaymentWithCandidateDto();
            
            // Map payment details
            dto.setId(p.getId());
            dto.setPaymentCode(p.getPaymentCode());
            dto.setPaymentType(p.getPaymentType());
            dto.setAmount(p.getAmount());
            dto.setPaymentDate(p.getPaymentDate());
            dto.setPaymentMode(p.getPaymentMode());
            dto.setAccountName(p.getAccountName());
            if (p.getAccountHolder() != null) dto.setAccountHolderName(p.getAccountHolder().getDisplayName());
            dto.setReferenceNumber(p.getReferenceNumber());
            
            // Map candidate details
            CandidateSummaryDto cdto = new CandidateSummaryDto();
            cdto.setId(p.getCandidate().getId());
            cdto.setCandidateName(p.getCandidate().getCandidateName());
            cdto.setCandidateCode(p.getCandidate().getCandidateCode());
            cdto.setMobileNumber(p.getCandidate().getMobileNumber());
            if (p.getCandidate().getAssignedTeam() != null) {
                cdto.setAssignedTeamId(p.getCandidate().getAssignedTeam().getId());
                cdto.setTeamName(p.getCandidate().getAssignedTeam().getName());
                cdto.setTeam(p.getCandidate().getAssignedTeam().getName());
            }
            
            dto.setCandidate(cdto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OutstandingRowDto> getOutstanding(String team, String bucket, boolean overdueOnly) {
        User currentUser = getCurrentUser();
        Collection<UUID> teamIds = currentUser.getRole() == UserRole.ADMIN ? null : 
            currentUser.getTeams().stream().map(Team::getId).collect(Collectors.toList());
            
        if (team != null && !team.isBlank()) {
            teamIds = List.of(UUID.fromString(team));
        }

        List<Candidate> candidates = teamIds != null ? 
            candidateRepository.findPlacedCandidatesByTeams(teamIds) :
            candidateRepository.findAllPlacedCandidates();
            
        List<OutstandingRowDto> rows = new ArrayList<>();
        
        for (Candidate c : candidates) {
            BigDecimal totalCollected = BigDecimal.ZERO;
            for (Payment p : c.getPayments()) {
                if (p.getPaymentType() != PaymentType.REFUND) {
                    totalCollected = totalCollected.add(p.getAmount());
                } else {
                    totalCollected = totalCollected.subtract(p.getAmount());
                }
            }
            
            BigDecimal commitment = c.getPlacement().getCommittedAmount() != null ? c.getPlacement().getCommittedAmount() : BigDecimal.ZERO;
            BigDecimal outstanding = commitment.subtract(totalCollected);
            
            if (outstanding.compareTo(BigDecimal.ZERO) > 0) {
                OutstandingRowDto dto = new OutstandingRowDto();
                
                CandidateSummaryDto cdto = new CandidateSummaryDto();
                cdto.setId(c.getId());
                cdto.setCandidateName(c.getCandidateName());
                cdto.setCandidateCode(c.getCandidateCode());
                cdto.setMobileNumber(c.getMobileNumber());
                if (c.getAssignedTeam() != null) {
                    cdto.setAssignedTeamId(c.getAssignedTeam().getId());
                    cdto.setTeamName(c.getAssignedTeam().getName());
                    cdto.setTeam(c.getAssignedTeam().getName());
                }
                
                dto.setCandidate(cdto);
                dto.setCompany(c.getPlacement().getCompanyName());
                dto.setPlacementDate(c.getPlacement().getPlacementDate());
                dto.setDueDate(c.getPlacement().getFinalAppliedDueDate());
                dto.setCtc(c.getPlacement().getAnnualCtc());
                dto.setCommitmentAmount(commitment);
                dto.setCollected(totalCollected);
                dto.setOutstanding(outstanding);
                
                // Simple ageing logic
                LocalDate dueDate = c.getPlacement().getFinalAppliedDueDate();
                if (dueDate != null) {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
                    dto.setAgeingDays((int) days);
                    if (days > 0) dto.setOverdueDays((int) days);
                }
                
                rows.add(dto);
            }
        }
        
        return rows;
    }

    @Transactional(readOnly = true)
    public List<CompanyReportRowDto> getCompanies(LocalDate from, LocalDate to, String team) {
        return new ArrayList<>(); // Dummy for now
    }

    @Transactional(readOnly = true)
    public String getCollectionsCsv(LocalDate from, LocalDate to, String team) {
        return "Dummy CSV Data";
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}
