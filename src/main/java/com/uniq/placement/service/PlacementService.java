package com.uniq.placement.service;

import com.uniq.placement.dto.placement.PlacementInputDto;
import com.uniq.placement.dto.placement.PlacementResponseDto;
import com.uniq.placement.entity.Candidate;
import com.uniq.placement.entity.Placement;
import com.uniq.placement.entity.Team;
import com.uniq.placement.entity.User;
import com.uniq.placement.entity.enums.CandidateStatus;
import com.uniq.placement.entity.enums.DuePeriod;
import com.uniq.placement.entity.enums.PlacementStatusEnum;
import com.uniq.placement.exception.ResourceNotFoundException;
import com.uniq.placement.repository.CandidateRepository;
import com.uniq.placement.repository.PlacementRepository;
import com.uniq.placement.repository.TeamRepository;
import com.uniq.placement.repository.UserRepository;
import com.uniq.placement.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlacementService {

    private final PlacementRepository placementRepository;
    private final CandidateRepository candidateRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Transactional
    public PlacementResponseDto savePlacement(UUID candidateId, PlacementInputDto dto) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        Placement placement = placementRepository.findByCandidateId(candidateId)
                .orElse(new Placement());

        placement.setCandidate(candidate);
        placement.setCompanyName(dto.getCompanyName());
        placement.setJobRole(dto.getJobRole());
        placement.setCompanyLocation(dto.getCompanyLocation());
        placement.setPlacementDate(dto.getPlacementDate());
        placement.setJoiningDate(dto.getJoiningDate());
        
        placement.setDuePeriodLabel(dto.getDuePeriod());
        int dueDays = dto.getDuePeriod() == DuePeriod.CUSTOM ? 
                (dto.getCustomDueDays() != null ? dto.getCustomDueDays() : 0) : 
                dto.getDuePeriod().toDays();
        placement.setDuePeriodDays(dueDays);
        
        LocalDate calcDueDate = DateUtils.calculateDueDate(dto.getJoiningDate(), dto.getDuePeriod(), dto.getCustomDueDays());
        placement.setAutoCalculatedDueDate(calcDueDate);
        placement.setFinalAppliedDueDate(dto.getDueDate() != null ? dto.getDueDate() : calcDueDate);
        
        placement.setAnnualCtc(dto.getAnnualCtc());
        placement.setCommittedPercentage(dto.getCommittedPercentage());
        
        BigDecimal committedAmount = dto.getAnnualCtc()
                .multiply(dto.getCommittedPercentage())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        placement.setCommittedAmount(committedAmount);
        
        placement.setPaymentTerms(dto.getPaymentTerms());
        placement.setReferralType(dto.getReferralType());
        
        Team shareTeam = teamRepository.findById(dto.getShareTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Share team not found"));
        placement.setShareTeam(shareTeam);
        
        placement.setRemarks(dto.getRemarks());
        
        if (placement.getId() == null) {
            placement.setStatus(PlacementStatusEnum.ACTIVE);
            placement.setCreatedBy(getCurrentUser());
        }
        placement.setUpdatedBy(getCurrentUser());

        Placement savedPlacement = placementRepository.save(placement);
        
        // Update candidate status
        if (candidate.getStatus() == CandidateStatus.REGISTERED || 
            candidate.getStatus() == CandidateStatus.TRAINING || 
            candidate.getStatus() == CandidateStatus.INTERVIEW_READY ||
            candidate.getStatus() == CandidateStatus.ATTENDING_INTERVIEWS) {
            candidate.setStatus(CandidateStatus.PLACED);
            candidateRepository.save(candidate);
        }

        return mapToDto(savedPlacement);
    }
    
    private PlacementResponseDto mapToDto(Placement p) {
        PlacementResponseDto pdto = new PlacementResponseDto();
        pdto.setId(p.getId());
        pdto.setCompanyName(p.getCompanyName());
        pdto.setJobRole(p.getJobRole());
        pdto.setCompanyLocation(p.getCompanyLocation());
        pdto.setPlacementDate(p.getPlacementDate());
        pdto.setJoiningDate(p.getJoiningDate());
        pdto.setDuePeriodDays(p.getDuePeriodDays());
        pdto.setDuePeriodLabel(p.getDuePeriodLabel());
        pdto.setAutoCalculatedDueDate(p.getAutoCalculatedDueDate());
        pdto.setFinalAppliedDueDate(p.getFinalAppliedDueDate());
        pdto.setAnnualCtc(p.getAnnualCtc());
        pdto.setCommittedPercentage(p.getCommittedPercentage());
        pdto.setCommittedAmount(p.getCommittedAmount());
        pdto.setPaymentTerms(p.getPaymentTerms());
        pdto.setReferralType(p.getReferralType());
        if (p.getShareTeam() != null) pdto.setShareTeamId(p.getShareTeam().getId());
        pdto.setStatus(p.getStatus());
        pdto.setOfferLetterUrl(p.getOfferLetterUrl());
        pdto.setRemarks(p.getRemarks());
        return pdto;
    }
    
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }
}
