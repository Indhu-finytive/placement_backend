package com.uniq.placement.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PaymentIdGenerator {

    private final AtomicInteger sequence = new AtomicInteger(100);

    public String generatePaymentId() {
        return "COL-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM")) + "-" + sequence.incrementAndGet();
    }
}
