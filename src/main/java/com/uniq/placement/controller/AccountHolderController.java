package com.uniq.placement.controller;

import com.uniq.placement.dto.account.AccountHolderInputDto;
import com.uniq.placement.dto.account.AccountHolderResponseDto;
import com.uniq.placement.dto.account.PaymentAccountSummaryDto;
import com.uniq.placement.dto.payment.PaymentResponseDto;
import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.service.AccountHolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountHolderController {

    private final AccountHolderService accountHolderService;

    @GetMapping("/account-holders")
    public ResponseEntity<List<AccountHolderResponseDto>> getAccountHolders(
            @RequestParam(required = false) ActiveStatus status) {
        return ResponseEntity.ok(accountHolderService.getAccountHolders(status));
    }

    @PostMapping("/account-holders")
    public ResponseEntity<AccountHolderResponseDto> createAccountHolder(
            @Valid @RequestBody AccountHolderInputDto request) {
        return new ResponseEntity<>(accountHolderService.createAccountHolder(request), HttpStatus.CREATED);
    }

    @PatchMapping("/account-holders/{accountHolderId}")
    public ResponseEntity<AccountHolderResponseDto> updateAccountHolder(
            @PathVariable UUID accountHolderId,
            @Valid @RequestBody AccountHolderInputDto request) {
        return ResponseEntity.ok(accountHolderService.updateAccountHolder(accountHolderId, request));
    }

    @GetMapping("/payment-accounts")
    public ResponseEntity<List<PaymentAccountSummaryDto>> getPaymentAccounts() {
        return ResponseEntity.ok(accountHolderService.getPaymentAccountSummaries());
    }

    @GetMapping("/payment-accounts/{accountName}/ledger")
    public ResponseEntity<List<PaymentResponseDto>> getAccountLedger(@PathVariable String accountName) {
        return ResponseEntity.ok(accountHolderService.getAccountLedger(accountName));
    }
}
