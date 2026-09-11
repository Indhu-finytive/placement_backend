package com.uniq.placement.controller;

import com.uniq.placement.dto.allocation.ShareAllocationInputDto;
import com.uniq.placement.dto.allocation.ShareAllocationResponseDto;
import com.uniq.placement.service.ShareAllocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/candidates/{candidateId}/payments/{paymentId}/allocation")
@RequiredArgsConstructor
public class ShareAllocationController {

    private final ShareAllocationService shareAllocationService;

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShareAllocationResponseDto> setShareAllocation(
            @PathVariable UUID candidateId, // Kept for path mapping
            @PathVariable UUID paymentId,
            @Valid @RequestBody ShareAllocationInputDto request) {
        return ResponseEntity.ok(shareAllocationService.saveAllocation(paymentId, request));
    }
}
