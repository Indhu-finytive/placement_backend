package com.uniq.placement.service;

import com.uniq.placement.dto.settlement.PartnerBalanceDto;
import com.uniq.placement.dto.settlement.SettlementCreateDto;
import com.uniq.placement.dto.settlement.SettlementResponseDto;
import com.uniq.placement.entity.AccountHolder;
import com.uniq.placement.entity.Settlement;
import com.uniq.placement.entity.User;
import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.entity.enums.SettlementDirection;
import com.uniq.placement.exception.BusinessRuleException;
import com.uniq.placement.exception.ResourceNotFoundException;
import com.uniq.placement.repository.AccountHolderRepository;
import com.uniq.placement.repository.SettlementRepository;
import com.uniq.placement.repository.ShareAllocationRepository;
import com.uniq.placement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final ShareAllocationRepository shareAllocationRepository;
    private final UserRepository userRepository;
    private final AccountHolderRepository accountHolderRepository;

    @Transactional(readOnly = true)
    public List<SettlementResponseDto> getSettlements(String partner, LocalDate from, LocalDate to) {
        return settlementRepository.findByFilters(partner, from, to).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SettlementResponseDto recordSettlement(SettlementCreateDto dto) {
        validateSettlement(dto);

        Settlement settlement = new Settlement();
        settlement.setPartner(dto.getPartner());
        settlement.setAmount(dto.getAmount());
        settlement.setSettlementDate(dto.getDate());
        settlement.setAccount(dto.getAccount());
        settlement.setDirection(dto.getDirection());
        settlement.setRemarks(dto.getRemarks());
        settlement.setEnteredBy(getCurrentUser());

        Settlement savedSettlement = settlementRepository.save(settlement);
        return mapToDto(savedSettlement);
    }

    private void validateSettlement(SettlementCreateDto dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Settlement amount must be greater than zero");
        }

        if (dto.getDirection() == null || dto.getDirection() == SettlementDirection.PAID_TO_PARTNER) {
            String partner = dto.getPartner();
            String account = dto.getAccount();
            if (partner != null && account != null) {
                String pLower = partner.trim().toLowerCase();
                String aLower = account.trim().toLowerCase();

                if (aLower.contains(pLower)) {
                    throw new BusinessRuleException("Cannot settle partner using partner's own account (" + account + "). Direct settlements must be paid from a Company account.");
                }

                if (accountHolderRepository != null) {
                    List<AccountHolder> holders = accountHolderRepository.findByStatus(ActiveStatus.ACTIVE);
                    for (AccountHolder ah : holders) {
                        String name = ah.getName() != null ? ah.getName().toLowerCase() : "";
                        String display = ah.getDisplayName() != null ? ah.getDisplayName().toLowerCase() : "";
                        if (aLower.equals(name) || aLower.equals(display) || aLower.contains(name) || (!display.isEmpty() && aLower.contains(display))) {
                            if (name.contains(pLower) || display.contains(pLower)) {
                                throw new BusinessRuleException("Cannot settle partner using partner's own account (" + account + "). Direct settlements must be paid from a Company account.");
                            }
                            if (ah.getLinkedTeam() != null) {
                                String teamName = ah.getLinkedTeam().getName().toLowerCase();
                                String teamBase = teamName.replace("team", "").trim();
                                if (teamBase.equals(pLower) || pLower.contains(teamBase) || teamName.contains(pLower)) {
                                    throw new BusinessRuleException("Cannot settle partner using partner's own account (" + account + "). Direct settlements must be paid from a Company account.");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<PartnerBalanceDto> getPartnerBalances() {
        Map<String, PartnerBalanceDto> balances = new HashMap<>();

        // Get earned amounts from share allocations
        List<Object[]> earnedSums = shareAllocationRepository.sumByPartner();
        for (Object[] row : earnedSums) {
            String partner = (String) row[0];
            BigDecimal earned = (BigDecimal) row[1];
            
            PartnerBalanceDto dto = balances.computeIfAbsent(partner, p -> createEmptyBalance(p));
            dto.setEarned(earned);
        }

        // Get paid/returned amounts from settlements
        List<Object[]> settlementSums = settlementRepository.sumByPartnerAndDirection();
        for (Object[] row : settlementSums) {
            String partner = (String) row[0];
            SettlementDirection dir = (SettlementDirection) row[1];
            BigDecimal amount = (BigDecimal) row[2];
            
            PartnerBalanceDto dto = balances.computeIfAbsent(partner, p -> createEmptyBalance(p));
            if (dir == SettlementDirection.PAID_TO_PARTNER) {
                dto.setPaid(amount);
            } else {
                dto.setReturned(amount);
            }
        }

        // Calculate closing balances
        for (PartnerBalanceDto dto : balances.values()) {
            // closingBalance = earned - paid + returned
            BigDecimal closing = dto.getEarned()
                    .subtract(dto.getPaid())
                    .add(dto.getReturned());
            dto.setClosingBalance(closing);
        }

        return new ArrayList<>(balances.values());
    }

    private PartnerBalanceDto createEmptyBalance(String partner) {
        PartnerBalanceDto dto = new PartnerBalanceDto();
        dto.setPartner(partner);
        dto.setEarned(BigDecimal.ZERO);
        dto.setReceivedDirectly(BigDecimal.ZERO); // Always 0 for now as per simple flow
        dto.setPaid(BigDecimal.ZERO);
        dto.setReturned(BigDecimal.ZERO);
        dto.setClosingBalance(BigDecimal.ZERO);
        return dto;
    }

    private SettlementResponseDto mapToDto(Settlement s) {
        SettlementResponseDto dto = new SettlementResponseDto();
        dto.setId(s.getId());
        dto.setPartner(s.getPartner());
        dto.setAmount(s.getAmount());
        dto.setSettlementDate(s.getSettlementDate());
        dto.setAccount(s.getAccount());
        dto.setDirection(s.getDirection());
        dto.setRemarks(s.getRemarks());
        if (s.getEnteredBy() != null) {
            dto.setEnteredByUserId(s.getEnteredBy().getId());
        }
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }
}
