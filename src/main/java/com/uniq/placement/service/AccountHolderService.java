package com.uniq.placement.service;

import com.uniq.placement.dto.account.AccountHolderInputDto;
import com.uniq.placement.dto.account.AccountHolderResponseDto;
import com.uniq.placement.dto.account.PaymentAccountSummaryDto;
import com.uniq.placement.dto.payment.PaymentResponseDto;
import com.uniq.placement.entity.AccountHolder;
import com.uniq.placement.entity.AccountHolderHistory;
import com.uniq.placement.entity.Team;
import com.uniq.placement.entity.User;
import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.entity.enums.LedgerType;
import com.uniq.placement.entity.enums.UserRole;
import com.uniq.placement.exception.ResourceNotFoundException;
import com.uniq.placement.repository.AccountHolderHistoryRepository;
import com.uniq.placement.repository.AccountHolderRepository;
import com.uniq.placement.repository.PaymentRepository;
import com.uniq.placement.repository.TeamRepository;
import com.uniq.placement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountHolderService {

    private final AccountHolderRepository accountHolderRepository;
    private final AccountHolderHistoryRepository accountHolderHistoryRepository;
    private final TeamRepository teamRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AccountHolderResponseDto> getAccountHolders(ActiveStatus status) {
        List<AccountHolder> accounts = status != null ? 
                accountHolderRepository.findByStatus(status) : 
                accountHolderRepository.findAll();
                
        User currentUser = getCurrentUser();
        if (currentUser != null && currentUser.getRole() != UserRole.ADMIN) {
            var allowedTeamIds = currentUser.getTeams().stream()
                .map(Team::getId)
                .collect(Collectors.toSet());
            var allowedTeamNames = currentUser.getTeams().stream()
                .map(t -> t.getName().toLowerCase())
                .collect(Collectors.toSet());
            accounts = accounts.stream()
                .filter(account -> account.getLinkedLedgerType() == LedgerType.COMPANY ||
                    account.getLinkedTeam() == null ||
                    (allowedTeamIds.contains(account.getLinkedTeam().getId()) ||
                     (account.getLinkedTeam().getName() != null &&
                      allowedTeamNames.contains(account.getLinkedTeam().getName().toLowerCase()))))
                .collect(Collectors.toList());
        }

        return accounts.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public AccountHolderResponseDto createAccountHolder(AccountHolderInputDto dto) {
        AccountHolder ah = new AccountHolder();
        ah.setName(dto.getName());
        ah.setDisplayName(dto.getDisplayName());
        ah.setLinkedLedgerType(dto.getLinkedLedgerType());
        
        if (dto.getLinkedTeam() != null) {
            Team team = teamRepository.findById(dto.getLinkedTeam())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
            ah.setLinkedTeam(team);
        }
        
        ah.setBank(dto.getBank());
        ah.setBranchName(dto.getBranchName());
        ah.setAccountNumber(dto.getAccountNumber());
        if (dto.getLast4() != null && !dto.getLast4().isBlank()) {
            ah.setLast4(dto.getLast4());
        } else if (dto.getAccountNumber() != null && dto.getAccountNumber().trim().length() >= 4) {
            String clean = dto.getAccountNumber().trim();
            ah.setLast4(clean.substring(clean.length() - 4));
        } else {
            ah.setLast4(null);
        }
        ah.setIfscCode(dto.getIfscCode());
        ah.setUpi(dto.getUpi());
        ah.setPaymentType(dto.getPaymentType());
        ah.setStatus(dto.getStatus());
        ah.setRemarks(dto.getRemarks());
        
        ah = accountHolderRepository.save(ah);
        saveHistory(ah.getId(), "Account holder created", "CREATE");
        
        return mapToDto(ah);
    }

    @Transactional
    public AccountHolderResponseDto updateAccountHolder(UUID id, AccountHolderInputDto dto) {
        AccountHolder ah = accountHolderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account holder not found"));
                
        if (dto.getName() != null) ah.setName(dto.getName());
        if (dto.getDisplayName() != null) ah.setDisplayName(dto.getDisplayName());
        if (dto.getLinkedLedgerType() != null) ah.setLinkedLedgerType(dto.getLinkedLedgerType());
        
        if (dto.getLinkedTeam() != null) {
            Team team = teamRepository.findById(dto.getLinkedTeam())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
            ah.setLinkedTeam(team);
        } else {
            ah.setLinkedTeam(null);
        }
        
        if (dto.getBank() != null) ah.setBank(dto.getBank());
        if (dto.getBranchName() != null) ah.setBranchName(dto.getBranchName());
        if (dto.getAccountNumber() != null) {
            ah.setAccountNumber(dto.getAccountNumber());
            if ((dto.getLast4() == null || dto.getLast4().isBlank()) && dto.getAccountNumber().trim().length() >= 4) {
                String clean = dto.getAccountNumber().trim();
                ah.setLast4(clean.substring(clean.length() - 4));
            }
        }
        if (dto.getLast4() != null) ah.setLast4(dto.getLast4());
        if (dto.getIfscCode() != null) ah.setIfscCode(dto.getIfscCode());
        if (dto.getUpi() != null) ah.setUpi(dto.getUpi());
        if (dto.getPaymentType() != null) ah.setPaymentType(dto.getPaymentType());
        if (dto.getStatus() != null) ah.setStatus(dto.getStatus());
        if (dto.getRemarks() != null) ah.setRemarks(dto.getRemarks());
        
        ah = accountHolderRepository.save(ah);
        saveHistory(ah.getId(), "Account holder updated", "UPDATE");
        
        return mapToDto(ah);
    }
    
    private void saveHistory(UUID accountHolderId, String message, String actionType) {
        String username = "system";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            username = auth.getName();
        }
        
        AccountHolderHistory history = AccountHolderHistory.builder()
                .accountHoldersId(accountHolderId)
                .userId(username)
                .actionBy(username)
                .actionType(actionType)
                .message(message)
                .actionAt(Instant.now())
                .build();
                
        accountHolderHistoryRepository.save(history);
    }
    
    @Transactional(readOnly = true)
    public List<PaymentAccountSummaryDto> getPaymentAccountSummaries() {
        List<Object[]> summaries = paymentRepository.getAccountSummaries();
        
        BigDecimal totalAllAccounts = BigDecimal.ZERO;
        for (Object[] row : summaries) {
            totalAllAccounts = totalAllAccounts.add((BigDecimal) row[2]);
        }
        
        final BigDecimal finalTotal = totalAllAccounts;
        
        return summaries.stream().map(row -> {
            PaymentAccountSummaryDto dto = new PaymentAccountSummaryDto();
            dto.setName((String) row[0]);
            dto.setTransactionCount((Long) row[1]);
            BigDecimal amount = (BigDecimal) row[2];
            dto.setTotalReceived(amount);
            
            if (finalTotal.compareTo(BigDecimal.ZERO) > 0) {
                dto.setPercentageOfCollection(amount.multiply(new BigDecimal("100")).divide(finalTotal, 2, RoundingMode.HALF_UP));
            } else {
                dto.setPercentageOfCollection(BigDecimal.ZERO);
            }
            
            Object lastTransactionDate = row[3];
            if (lastTransactionDate instanceof LocalDate date) {
                dto.setLastTransactionDate(date);
            } else if (lastTransactionDate instanceof java.sql.Date date) {
                dto.setLastTransactionDate(date.toLocalDate());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getAccountLedger(String accountName) {
        return paymentRepository.findByAccountName(accountName).stream()
                .map(paymentService::mapToDto)
                .collect(Collectors.toList());
    }

    private AccountHolderResponseDto mapToDto(AccountHolder ah) {
        AccountHolderResponseDto dto = new AccountHolderResponseDto();
        dto.setId(ah.getId());
        dto.setName(ah.getName());
        dto.setDisplayName(ah.getDisplayName());
        dto.setLinkedLedgerType(ah.getLinkedLedgerType());
        if (ah.getLinkedTeam() != null) {
            dto.setLinkedTeam(ah.getLinkedTeam().getName());
            dto.setLinkedTeamId(ah.getLinkedTeam().getId());
        }
        dto.setBank(ah.getBank());
        dto.setBranchName(ah.getBranchName());
        dto.setAccountNumber(ah.getAccountNumber());
        dto.setLast4(ah.getLast4());
        dto.setIfscCode(ah.getIfscCode());
        dto.setUpi(ah.getUpi());
        dto.setPaymentType(ah.getPaymentType());
        dto.setStatus(ah.getStatus());
        dto.setRemarks(ah.getRemarks());
        return dto;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || "anonymousUser".equalsIgnoreCase(authentication.getName())) {
            return null;
        }
        return userRepository.findByUsername(authentication.getName()).orElse(null);
    }
}
