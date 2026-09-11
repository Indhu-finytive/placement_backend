package com.uniq.placement.service;

import com.uniq.placement.dto.allocation.ShareAllocationInputDto;
import com.uniq.placement.dto.allocation.ShareAllocationResponseDto;
import com.uniq.placement.entity.Payment;
import com.uniq.placement.entity.ShareAllocation;
import com.uniq.placement.entity.User;
import com.uniq.placement.exception.BusinessRuleException;
import com.uniq.placement.exception.ResourceNotFoundException;
import com.uniq.placement.repository.PaymentRepository;
import com.uniq.placement.repository.ShareAllocationRepository;
import com.uniq.placement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShareAllocationService {

    private final ShareAllocationRepository allocationRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Transactional
    public ShareAllocationResponseDto saveAllocation(UUID paymentId, ShareAllocationInputDto dto) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (dto.getAmount().compareTo(payment.getAmount()) > 0) {
            throw new BusinessRuleException("Allocation amount cannot exceed payment amount");
        }

        ShareAllocation allocation = allocationRepository.findByPaymentId(paymentId)
                .orElse(new ShareAllocation());

        allocation.setPayment(payment);
        allocation.setPartner(dto.getPartner());
        allocation.setAppliedPercent(dto.getAppliedPercent());
        allocation.setAmount(dto.getAmount());
        allocation.setAllocationDate(dto.getDate());
        allocation.setRemarks(dto.getRemarks());

        if (allocation.getId() == null) {
            allocation.setAllocatedBy(getCurrentUser());
            allocation.setAllocatedAt(Instant.now());
        }

        ShareAllocation savedAllocation = allocationRepository.save(allocation);

        return mapToDto(savedAllocation);
    }

    private ShareAllocationResponseDto mapToDto(ShareAllocation sa) {
        ShareAllocationResponseDto dto = new ShareAllocationResponseDto();
        dto.setId(sa.getId());
        dto.setPartner(sa.getPartner());
        dto.setAppliedPercent(sa.getAppliedPercent());
        dto.setAmount(sa.getAmount());
        dto.setAllocationDate(sa.getAllocationDate());
        dto.setRemarks(sa.getRemarks());
        if (sa.getAllocatedBy() != null) {
            dto.setAllocatedByUserId(sa.getAllocatedBy().getId());
        }
        dto.setAllocatedAt(sa.getAllocatedAt());
        return dto;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }
}
