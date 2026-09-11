package com.uniq.placement.controller;

import com.uniq.placement.dto.allocation.ShareAllocationInputDto;
import com.uniq.placement.dto.payment.CandidatePaymentsDto;
import com.uniq.placement.dto.payment.PaymentCreateDto;
import com.uniq.placement.dto.payment.PaymentResponseDto;
import com.uniq.placement.service.PaymentService;
// import com.uniq.placement.service.ShareAllocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/candidates/{candidateId}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<CandidatePaymentsDto> getCandidatePayments(@PathVariable UUID candidateId) {
        return ResponseEntity.ok(paymentService.getCandidatePayments(candidateId));
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> recordPayment(
            @PathVariable UUID candidateId,
            @Valid @RequestBody PaymentCreateDto request) {
        return new ResponseEntity<>(paymentService.recordPayment(candidateId, request), HttpStatus.CREATED);
    }
}
