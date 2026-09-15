package com.uniq.placement.util;

import com.uniq.placement.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class PaymentIdGenerator {

    private final PaymentRepository paymentRepository;

    public synchronized String generatePaymentId() {
        String prefix = "COL-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
        int maxSeq = paymentRepository.findMaxSequenceByPrefix(prefix);
        int nextSeq = Math.max(maxSeq, 100) + 1;
        return prefix + "-" + nextSeq;
    }
}
