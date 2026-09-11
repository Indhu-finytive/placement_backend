package com.uniq.placement.controller;

import com.uniq.placement.dto.settlement.PartnerBalanceDto;
import com.uniq.placement.dto.settlement.SettlementCreateDto;
import com.uniq.placement.dto.settlement.SettlementResponseDto;
import com.uniq.placement.service.SettlementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @GetMapping("/settlements")
    public ResponseEntity<List<SettlementResponseDto>> getSettlements(
            @RequestParam(required = false) String partner,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(settlementService.getSettlements(partner, from, to));
    }

    @PostMapping("/settlements")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SettlementResponseDto> recordSettlement(@Valid @RequestBody SettlementCreateDto request) {
        return new ResponseEntity<>(settlementService.recordSettlement(request), HttpStatus.CREATED);
    }

    @GetMapping("/partner-balances")
    public ResponseEntity<List<PartnerBalanceDto>> getPartnerBalances() {
        return ResponseEntity.ok(settlementService.getPartnerBalances());
    }
}
