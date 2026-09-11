package com.uniq.placement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "share_allocations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ShareAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;

    @Column(nullable = false, length = 150)
    private String partner;

    @Column(name = "applied_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal appliedPercent;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "allocation_date", nullable = false)
    private LocalDate allocationDate;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocated_by")
    private User allocatedBy;

    @Column(name = "allocated_at")
    private Instant allocatedAt;
}
