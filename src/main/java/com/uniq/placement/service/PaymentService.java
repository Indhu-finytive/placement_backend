package com.uniq.placement.service;

import com.uniq.placement.dto.allocation.ShareAllocationResponseDto;
import com.uniq.placement.dto.payment.CandidatePaymentsDto;
import com.uniq.placement.dto.payment.PaymentCreateDto;
import com.uniq.placement.dto.payment.PaymentResponseDto;
import com.uniq.placement.entity.AccountHolder;
import com.uniq.placement.entity.Candidate;
import com.uniq.placement.entity.Payment;
import com.uniq.placement.entity.User;
import com.uniq.placement.entity.enums.PaymentMode;
import com.uniq.placement.entity.enums.PaymentType;
import com.uniq.placement.exception.BusinessRuleException;
import com.uniq.placement.exception.ResourceNotFoundException;
import com.uniq.placement.repository.AccountHolderRepository;
import com.uniq.placement.repository.CandidateRepository;
import com.uniq.placement.repository.PaymentRepository;
import com.uniq.placement.repository.UserRepository;
import com.uniq.placement.util.PaymentIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CandidateRepository candidateRepository;
    private final AccountHolderRepository accountHolderRepository;
    private final UserRepository userRepository;
    private final PaymentIdGenerator paymentIdGenerator;

    @Transactional(readOnly = true)
    public CandidatePaymentsDto getCandidatePayments(UUID candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
                
        List<Payment> payments = paymentRepository.findByCandidateIdOrderByPaymentDateDesc(candidateId);
        
        BigDecimal totalCollected = BigDecimal.ZERO;
        BigDecimal totalShareAllocated = BigDecimal.ZERO;
        
        List<PaymentResponseDto> items = payments.stream().map(p -> {
            PaymentResponseDto dto = mapToDto(p);
            return dto;
        }).collect(Collectors.toList());
        
        for (Payment p : payments) {
            if (p.getPaymentType() != PaymentType.REFUND) {
                totalCollected = totalCollected.add(p.getAmount());
            } else {
                totalCollected = totalCollected.subtract(p.getAmount());
            }
            
            if (p.getAllocation() != null) {
                totalShareAllocated = totalShareAllocated.add(p.getAllocation().getAmount());
            }
        }
        
        BigDecimal outstanding = null;
        if (candidate.getPlacement() != null && candidate.getPlacement().getCommittedAmount() != null) {
            outstanding = candidate.getPlacement().getCommittedAmount().subtract(totalCollected);
        }

        CandidatePaymentsDto result = new CandidatePaymentsDto();
        result.setItems(items);
        result.setTotalCollected(totalCollected);
        result.setTotalShareAllocated(totalShareAllocated);
        result.setCandidateOutstanding(outstanding);
        
        return result;
    }

    @Transactional
    public PaymentResponseDto recordPayment(UUID candidateId, PaymentCreateDto dto) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
                
        AccountHolder accountHolder = null;
        if (dto.getAccountHolderId() != null) {
            accountHolder = accountHolderRepository.findById(dto.getAccountHolderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account holder not found"));
        } else if (dto.getPaymentMode() != PaymentMode.CASH) {
            throw new BusinessRuleException("Account holder is required for non-cash payment");
        }
                
        User currentUser = getCurrentUser();
        
        Payment payment = new Payment();
        payment.setPaymentCode(paymentIdGenerator.generatePaymentId());
        payment.setCandidate(candidate);
        payment.setPlacement(candidate.getPlacement());
        payment.setPaymentType(dto.getPaymentType());
        payment.setAmount(dto.getAmount());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentMode(dto.getPaymentMode());
        payment.setAccountName(dto.getAccountName());
        payment.setAccountHolder(accountHolder);
        payment.setReferenceNumber(dto.getReferenceNumber());
        payment.setRemarks(dto.getRemarks());
        payment.setReceivedBy(currentUser);
        
        Payment savedPayment = paymentRepository.save(payment);
        return mapToDto(savedPayment);
    }
    
    public PaymentResponseDto mapToDto(Payment payment) {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(payment.getId());
        dto.setPaymentCode(payment.getPaymentCode());
        dto.setPaymentType(payment.getPaymentType());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMode(payment.getPaymentMode());
        dto.setAccountName(payment.getAccountName());
        if (payment.getAccountHolder() != null) dto.setAccountHolderName(payment.getAccountHolder().getDisplayName());
        dto.setReferenceNumber(payment.getReferenceNumber());
        dto.setRemarks(payment.getRemarks());
        
        if (payment.getReceivedBy() != null) {
            dto.setReceivedById(payment.getReceivedBy().getId());
            dto.setReceivedByName(payment.getReceivedBy().getFullName());
        }
        
        if (payment.getAllocation() != null) {
            ShareAllocationResponseDto adto = new ShareAllocationResponseDto();
            adto.setId(payment.getAllocation().getId());
            adto.setPartner(payment.getAllocation().getPartner());
            adto.setAppliedPercent(payment.getAllocation().getAppliedPercent());
            adto.setAmount(payment.getAllocation().getAmount());
            adto.setAllocationDate(payment.getAllocation().getAllocationDate());
            adto.setRemarks(payment.getAllocation().getRemarks());
            if (payment.getAllocation().getAllocatedBy() != null) {
                adto.setAllocatedByUserId(payment.getAllocation().getAllocatedBy().getId());
            }
            adto.setAllocatedAt(payment.getAllocation().getAllocatedAt());
            dto.setAllocation(adto);
        }
        
        return dto;
    }
    
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }
}
