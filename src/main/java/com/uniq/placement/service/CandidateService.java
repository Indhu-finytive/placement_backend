package com.uniq.placement.service;

import com.uniq.placement.dto.candidate.*;
import com.uniq.placement.dto.common.EnumOptionDto;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final BatchRepository batchRepository;
    private final BranchRepository branchRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final CandidateCodeGenerator codeGenerator;
    private final PaymentService paymentService;
    private final CandidateHistoryService candidateHistoryService;

    @Transactional(readOnly = true)
    public PageDto<CandidateResponseDto> getCandidates(String search, String team, CandidateStatus status, Eligibility eligibility, String course, int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(pageSize, 1);
        Pageable pageable = PageRequest.of(safePage - 1, safePageSize);
        
        User currentUser = getCurrentUser();
        boolean isFullAccess = currentUser.getRole() == UserRole.ADMIN || 
                               currentUser.getAccess() == AccessLevel.FULL_ACCESS;
        Collection<UUID> teamIds = isFullAccess ? null : 
            currentUser.getTeams().stream().map(Team::getId).collect(Collectors.toList());
            
        UUID teamIdFilter = null;
        if (team != null && !team.isBlank() && !"All teams".equalsIgnoreCase(team) && !"All Teams".equalsIgnoreCase(team)) {
            try {
                teamIdFilter = UUID.fromString(team);
            } catch (IllegalArgumentException e) {
                teamIdFilter = teamRepository.findAll().stream()
                        .filter(t -> t.getName().equalsIgnoreCase(team.trim()))
                        .map(Team::getId)
                        .findFirst()
                        .orElse(null);
            }
        }

        Page<Candidate> candidatePage;
        if (isFullAccess) {
            candidatePage = candidateRepository.findAllWithFilters(search, teamIdFilter, status, eligibility, course, pageable);
        } else if (teamIds != null && !teamIds.isEmpty()) {
            candidatePage = candidateRepository.findAllWithFilters(search, teamIdFilter, status, eligibility, course, teamIds, pageable);
        } else {
            // User has no assigned teams yet: show all candidates or unassigned candidates
            candidatePage = candidateRepository.findAllWithFilters(search, teamIdFilter, status, eligibility, course, pageable);
        }

        List<CandidateResponseDto> dtos = candidatePage.getContent().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return new PageDto<>(dtos, safePage, safePageSize, candidatePage.getTotalElements());
    }

    @Transactional
    public CandidateResponseDto createCandidate(CandidateCreateDto dto) {
        if (candidateRepository.existsByMobileNumber(dto.getMobileNumber())) {
            throw new DuplicateResourceException("Candidate with mobile " + dto.getMobileNumber() + " already exists");
        }

        User currentUser = getCurrentUser();
        UUID requestedTeamId = dto.getAssignedTeamId();
        UUID assignedTeamId = currentUser.getRole() == UserRole.ADMIN
            ? requestedTeamId
            : getOnlyAllowedTeamId(currentUser, requestedTeamId);
        Team assignedTeam = teamRepository.findById(assignedTeamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        Branch branch = resolveBranch(dto.getBranch(), assignedTeam);
        Batch batch = resolveBatch(dto.getBatch(), dto.getBatchId(), branch, assignedTeam, dto.getCourse(), dto.getBatchType(), dto.getTrainer());

        Candidate candidate = new Candidate();
        candidate.setCandidateCode(codeGenerator.generateCode(dto.getCourse()));
        candidate.setBatch(batch);
        candidate.setAssignedTeam(assignedTeam);
        candidate.setBranch(branch != null ? branch : batch.getBranch());
        
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
        
        candidate.setCreatedBy(currentUser);
        candidate.setUpdatedBy(currentUser);

        Candidate savedCandidate = candidateRepository.save(candidate);

        // Log history for candidate registration
        candidateHistoryService.log(
                savedCandidate.getId(),
                currentUser.getUsername(),
                "CREATE",
                "Candidate registered"
        );
        
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
        assertTeamAccess(candidate.getAssignedTeam(), getCurrentUser());
        return mapToResponseDto(candidate);
    }

    @Transactional
    public CandidateResponseDto updateCandidate(UUID id, CandidateUpdateDto dto) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        User currentUser = getCurrentUser();
        assertTeamAccess(candidate.getAssignedTeam(), currentUser);

        // Capture old values for change detection
        CandidateStatus oldStatus = candidate.getStatus();
        String oldCourse = candidate.getCourse();
        Eligibility oldEligibility = candidate.getEligibility();

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
            UUID assignedTeamId = currentUser.getRole() == UserRole.ADMIN
                ? dto.getAssignedTeamId()
                : getOnlyAllowedTeamId(currentUser, dto.getAssignedTeamId());
            Team assignedTeam = teamRepository.findById(assignedTeamId)
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
            candidate.setAssignedTeam(assignedTeam);
        }

        if (dto.getBranch() != null) {
            candidate.setBranch(resolveBranch(dto.getBranch(), candidate.getAssignedTeam()));
        }

        if (dto.getBatchId() != null || (dto.getBatch() != null && !dto.getBatch().isBlank())) {
            Batch batch = resolveBatch(
                    dto.getBatch(),
                    dto.getBatchId(),
                    candidate.getBranch(),
                    candidate.getAssignedTeam(),
                    candidate.getCourse(),
                    candidate.getBatchType(),
                    candidate.getTrainer());
            candidate.setBatch(batch);
            if (candidate.getBranch() == null) candidate.setBranch(batch.getBranch());
        }

        candidate.setUpdatedBy(currentUser);
        
        Candidate savedCandidate = candidateRepository.save(candidate);

        // Log history for changes
        logCandidateChanges(savedCandidate.getId(), currentUser.getUsername(),
                oldStatus, candidate.getStatus(),
                oldCourse, candidate.getCourse(),
                oldEligibility, candidate.getEligibility());

        return mapToResponseDto(savedCandidate);
    }

    public RegistrationOptionsDto getRegistrationOptions() {
        List<EnumOptionDto> candidateStatuses = Arrays.stream(CandidateStatus.values())
                .map(s -> new EnumOptionDto(s.name(), s.getValue()))
                .collect(Collectors.toList());

        List<EnumOptionDto> eligibilities = Arrays.stream(Eligibility.values())
                .map(e -> new EnumOptionDto(e.name(), e.getValue()))
                .collect(Collectors.toList());

        List<EnumOptionDto> courses = Arrays.stream(Course.values())
                .map(c -> new EnumOptionDto(c.name(), c.getValue()))
                .collect(Collectors.toList());

        return new RegistrationOptionsDto(candidateStatuses, eligibilities, courses);
    }

    private void logCandidateChanges(UUID candidateId, String username,
                                      CandidateStatus oldStatus, CandidateStatus newStatus,
                                      String oldCourse, String newCourse,
                                      Eligibility oldEligibility, Eligibility newEligibility) {
        if (oldStatus != newStatus && newStatus != null) {
            String oldLabel = oldStatus != null ? oldStatus.getValue() : "None";
            candidateHistoryService.log(candidateId, username, "STATUS_CHANGE",
                    "Candidate status changed from " + oldLabel + " to " + newStatus.getValue());
        }

        if (oldCourse != null && newCourse != null && !oldCourse.equals(newCourse)) {
            candidateHistoryService.log(candidateId, username, "COURSE_CHANGE",
                    "Candidate course changed from " + oldCourse + " to " + newCourse);
        } else if (oldCourse == null && newCourse != null) {
            candidateHistoryService.log(candidateId, username, "COURSE_CHANGE",
                    "Candidate course changed from None to " + newCourse);
        }

        if (oldEligibility != newEligibility && newEligibility != null) {
            String oldLabel = oldEligibility != null ? oldEligibility.getValue() : "None";
            candidateHistoryService.log(candidateId, username, "ELIGIBILITY_CHANGE",
                    "Candidate eligibility changed from " + oldLabel + " to " + newEligibility.getValue());
        }
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    private UUID getOnlyAllowedTeamId(User user, UUID requestedTeamId) {
        if (requestedTeamId == null) {
            throw new org.springframework.security.access.AccessDeniedException("A team is required");
        }
        boolean allowed = user.getTeams().stream()
                .anyMatch(team -> team.getId().equals(requestedTeamId));
        if (!allowed) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You can only manage candidates in your assigned team");
        }
        return requestedTeamId;
    }

    private void assertTeamAccess(Team team, User user) {
        if (user.getRole() != UserRole.ADMIN &&
                (team == null || user.getTeams().stream().noneMatch(allowed -> allowed.getId().equals(team.getId())))) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You can only access candidates in your assigned team");
        }
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
        if (candidate.getBatch() != null) dto.setBatch(candidate.getBatch().getName());
        dto.setBatchType(candidate.getBatchType());
        if (candidate.getBranch() != null) {
            dto.setBranch(candidate.getBranch().getName());
            dto.setBranchName(candidate.getBranch().getName());
        }
        dto.setTrainer(candidate.getTrainer());
        if (candidate.getAssignedTeam() != null) {
            dto.setAssignedTeamId(candidate.getAssignedTeam().getId());
            dto.setTeamName(candidate.getAssignedTeam().getName());
            dto.setTeam(candidate.getAssignedTeam().getName());
        }
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

    private Branch resolveBranch(String branchName, Team assignedTeam) {
        if (branchName != null && !branchName.isBlank()) {
            return branchRepository.findFirstByNameIgnoreCase(branchName.trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + branchName));
        }
        return assignedTeam != null ? assignedTeam.getBranch() : null;
    }

    private Batch resolveBatch(String batchName, UUID batchId, Branch branch, Team team, String course, TrainingMode batchType, String trainer) {
        if (batchId != null) {
            return batchRepository.findById(batchId)
                    .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));
        }
        String normalizedBatchName = batchName == null ? "" : batchName.trim();
        if (normalizedBatchName.isEmpty()) {
            throw new BusinessRuleException("Batch is required");
        }
        return batchRepository.findFirstByNameIgnoreCase(normalizedBatchName)
                .orElseGet(() -> {
                    Batch batch = new Batch();
                    batch.setName(normalizedBatchName);
                    batch.setBranch(branch);
                    batch.setTeam(team);
                    batch.setCourseName(course);
                    batch.setTrainingMode(batchType != null ? batchType : TrainingMode.OFFLINE);
                    batch.setTrainerName(trainer);
                    batch.setIsActive(true);
                    return batchRepository.save(batch);
                });
    }
}
