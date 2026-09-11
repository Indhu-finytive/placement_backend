package com.uniq.placement.controller;

import com.uniq.placement.dto.branch.BranchHistoryResponseDto;
import com.uniq.placement.dto.branch.BranchInputDto;
import com.uniq.placement.dto.branch.BranchResponseDto;
import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.service.BranchHistoryService;
import com.uniq.placement.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;
    private final BranchHistoryService branchHistoryService;

    @GetMapping
    public ResponseEntity<List<BranchResponseDto>> getBranches(
            @RequestParam(required = false) ActiveStatus status) {
        return ResponseEntity.ok(branchService.getBranches(status));
    }

    @GetMapping("/{branchId}/history")
    public ResponseEntity<List<BranchHistoryResponseDto>> getBranchHistory(@PathVariable UUID branchId) {
        return ResponseEntity.ok(branchHistoryService.getHistory(branchId));
    }

    @PostMapping
    public ResponseEntity<BranchResponseDto> createBranch(@Valid @RequestBody BranchInputDto request) {
        return new ResponseEntity<>(branchService.createBranch(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{branchId}")
    public ResponseEntity<BranchResponseDto> updateBranch(
            @PathVariable UUID branchId,
            @Valid @RequestBody BranchInputDto request) {
        return ResponseEntity.ok(branchService.updateBranch(branchId, request));
    }

    @PatchMapping("/{branchId}/status")
    public ResponseEntity<BranchResponseDto> updateStatus(
            @PathVariable UUID branchId,
            @RequestParam ActiveStatus status) {
        return ResponseEntity.ok(branchService.toggleBranch(branchId, status));
    }
}
