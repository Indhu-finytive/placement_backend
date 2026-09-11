package com.uniq.placement.service;

import com.uniq.placement.dto.candidate.*;
import com.uniq.placement.dto.common.PageDto;
import com.uniq.placement.dto.payment.PaymentCreateDto;
import com.uniq.placement.dto.payment.PaymentResponseDto;
import com.uniq.placement.dto.placement.PlacementResponseDto;
import com.uniq.placement.entity.*;
import com.uniq.placement.entity.enums.*;
import com.uniq.placement.exception.BusinessRuleException;
import com.uniq.placement.exception.DuplicateResourceException;
import com.uniq.placement.exception.ResourceNotFoundException;
import com.uniq.placement.repository.*;
import com.uniq.placement.util.CandidateCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final BatchRepository batchRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final CandidateCodeGenerator codeGenerator;
    private final PaymentService paymentService;

    @Transactional(readOnly = true)
    public PageDto<CandidateResponseDto> getCandidates(String search, String team, CandidateStatus status, Eligibility eligibility, String course, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        
        User currentUser = getCurrentUser();
        Collection<UUID> teamIds = currentUser.getRole() == UserRole.ADMIN ? null : 
            currentUser.getTeams().stream().map(Team::getId).collect(Collectors.toList());
            
        UUID teamIdFilter = team != null && !team.isBlank() ? UUID.fromString(team) : null;

        Page<Candidate> candidatePage = candidateRepository.findAllWithFilters(
                search, teamIdFilter, status, eligibility, course, teamIds, pageable);

        List<CandidateResponseDto> dtos = candidatePage.getContent().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return new PageDto<>(dtos, page, pageSize, candidatePage.getTotalElements());
    }

    @Transactional
    public CandidateResponseDto createCandidate(CandidateCreateDto dto) {
        if (candidateRepository.existsByMobileNumber(dto.getMobileNumber())) {
            throw new DuplicateResourceException("Candidate with mobile " + dto.getMobileNumber() + " already exists");
        }

        Batch batch = batchRepository.findById(dto.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));
                
        Team assignedTeam = teamRepository.findById(dto.getAssignedTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        Candidate candidate = new Candidate();
        candidate.setCandidateCode(codeGenerator.generateCode(dto.getCourse()));
        candidate.setBatch(batch);
        candidate.setAssignedTeam(assignedTeam);
        candidate.setBranch(batch.getBranch());
        
        candidate.setCandidateName(dto.getCandidateName());
        candidate.setMobileNumber(dto.getMobileNumber());
        candidate.setAlternateMobile(dto.getAlternateMobile());
        candidate.setEmail(dto.getEmail());
        candidate.setJoiningDate(dto.getJoiningDate());
        candidate.setGender(dto.getGender());
        candidate.setQualification(dto.getQualification());
        candidate.setDegree(dto.getDegree());
        candidate.setDepartment(dto.getDepartment());
        candidate.setPassingYear(dto.getPassingYear());
        candidate.setCollegeName(dto.getCollegeName());
        candidate.setCurrentLocation(dto.getCurrentLocation());
        candidate.setCourse(dto.getCourse());
        candidate.setBatchType(dto.getBatchType());
        candidate.setTrainer(dto.getTrainer());
        
        candidate.setStatus(dto.getStatus() != null ? dto.getStatus() : CandidateStatus.REGISTERED);
        candidate.setEligibility(dto.getEligibility() != null ? dto.getEligibility() : Eligibility.ELIGIBLE);
        candidate.setRemarks(dto.getRemarks());
        
        User currentUser = getCurrentUser();
        candidate.setCreatedBy(currentUser);
        candidate.setUpdatedBy(currentUser);

        Candidate savedCandidate = candidateRepository.save(candidate);
        
        // Handle initial document fee if provided
        if (dto.getInitialDocumentFee() != null) {
            InitialDocumentFeeDto feeDto = dto.getInitialDocumentFee();
            PaymentCreateDto paymentDto = new PaymentCreateDto();
            paymentDto.setPaymentDate(feeDto.getDate());
            paymentDto.setPaymentType(PaymentType.DOCUMENT_FEE);
            paymentDto.setAmount(feeDto.getAmount());
            paymentDto.setPaymentMode(feeDto.getMode());
            paymentDto.setAccountName(feeDto.getAccount());
            paymentDto.setAccountHolderId(feeDto.getAccountHolderId());
            paymentDto.setReferenceNumber(feeDto.getPaymentReferenceId());
            paymentDto.setRemarks(feeDto.getRemarks());
            
            paymentService.recordPayment(savedCandidate.getId(), paymentDto);
            
            // Refresh candidate to include payment
            savedCandidate = candidateRepository.findById(savedCandidate.getId()).orElse(savedCandidate);
        }

        return mapToResponseDto(savedCandidate);
    }

    @Transactional(readOnly = true)
    public CandidateResponseDto getCandidate(UUID id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        return mapToResponseDto(candidate);
    }

    @Transactional
    public CandidateResponseDto updateCandidate(UUID id, CandidateUpdateDto dto) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        if (dto.getCandidateName() != null) candidate.setCandidateName(dto.getCandidateName());
        if (dto.getMobileNumber() != null && !dto.getMobileNumber().equals(candidate.getMobileNumber())) {
            if (candidateRepository.existsByMobileNumber(dto.getMobileNumber())) {
                throw new DuplicateResourceException("Candidate with mobile " + dto.getMobileNumber() + " already exists");
            }
            candidate.setMobileNumber(dto.getMobileNumber());
        }
        if (dto.getAlternateMobile() != null) candidate.setAlternateMobile(dto.getAlternateMobile());
        if (dto.getEmail() != null) candidate.setEmail(dto.getEmail());
        if (dto.getJoiningDate() != null) candidate.setJoiningDate(dto.getJoiningDate());
        if (dto.getGender() != null) candidate.setGender(dto.getGender());
        if (dto.getQualification() != null) candidate.setQualification(dto.getQualification());
        if (dto.getDegree() != null) candidate.setDegree(dto.getDegree());
        if (dto.getDepartment() != null) candidate.setDepartment(dto.getDepartment());
        if (dto.getPassingYear() != null) candidate.setPassingYear(dto.getPassingYear());
        if (dto.getCollegeName() != null) candidate.setCollegeName(dto.getCollegeName());
        if (dto.getCurrentLocation() != null) candidate.setCurrentLocation(dto.getCurrentLocation());
        if (dto.getCourse() != null) candidate.setCourse(dto.getCourse());
        if (dto.getBatchType() != null) candidate.setBatchType(dto.getBatchType());
        if (dto.getTrainer() != null) candidate.setTrainer(dto.getTrainer());
        if (dto.getStatus() != null) candidate.setStatus(dto.getStatus());
        if (dto.getEligibility() != null) candidate.setEligibility(dto.getEligibility());
        if (dto.getRemarks() != null) candidate.setRemarks(dto.getRemarks());

        if (dto.getAssignedTeamId() != null) {
            Team assignedTeam = teamRepository.findById(dto.getAssignedTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
            candidate.setAssignedTeam(assignedTeam);
        }

        if (dto.getBatchId() != null) {
            Batch batch = batchRepository.findById(dto.getBatchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));
            candidate.setBatch(batch);
            candidate.setBranch(batch.getBranch());
        }

        candidate.setUpdatedBy(getCurrentUser());
        
        Candidate savedCandidate = candidateRepository.save(candidate);
        return mapToResponseDto(savedCandidate);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    private CandidateResponseDto mapToResponseDto(Candidate candidate) {
        CandidateResponseDto dto = new CandidateResponseDto();
        dto.setId(candidate.getId());
        dto.setCandidateCode(candidate.getCandidateCode());
        dto.setCandidateName(candidate.getCandidateName());
        dto.setMobileNumber(candidate.getMobileNumber());
        dto.setAlternateMobile(candidate.getAlternateMobile());
        dto.setEmail(candidate.getEmail());
        dto.setJoiningDate(candidate.getJoiningDate());
        dto.setGender(candidate.getGender());
        dto.setQualification(candidate.getQualification());
        dto.setDegree(candidate.getDegree());
        dto.setDepartment(candidate.getDepartment());
        dto.setPassingYear(candidate.getPassingYear());
        dto.setCollegeName(candidate.getCollegeName());
        dto.setCurrentLocation(candidate.getCurrentLocation());
        dto.setCourse(candidate.getCourse());
        dto.setBatchType(candidate.getBatchType());
        if (candidate.getBranch() != null) dto.setBranchName(candidate.getBranch().getName());
        dto.setTrainer(candidate.getTrainer());
        if (candidate.getAssignedTeam() != null) dto.setAssignedTeamId(candidate.getAssignedTeam().getId());
        dto.setStatus(candidate.getStatus());
        dto.setEligibility(candidate.getEligibility());
        dto.setRemarks(candidate.getRemarks());

        // Map placement
        if (candidate.getPlacement() != null) {
            Placement p = candidate.getPlacement();
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
            dto.setPlacement(pdto);
        }

        // Payments & Totals
        BigDecimal totalCollected = BigDecimal.ZERO;
        if (candidate.getPayments() != null) {
            List<PaymentResponseDto> payments = candidate.getPayments().stream()
                    .map(paymentService::mapToDto)
                    .collect(Collectors.toList());
            dto.setPayments(payments);
            
            for (Payment p : candidate.getPayments()) {
                if (p.getPaymentType() != PaymentType.REFUND) {
                    totalCollected = totalCollected.add(p.getAmount());
                } else {
                    totalCollected = totalCollected.subtract(p.getAmount());
                }
            }
        }
        dto.setTotalCollected(totalCollected);

        // Outstanding
        if (candidate.getPlacement() != null && candidate.getPlacement().getCommittedAmount() != null) {
            dto.setOutstanding(candidate.getPlacement().getCommittedAmount().subtract(totalCollected));
        }

        return dto;
    }
}
