package com.uniq.placement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "placement_due_date_overrides")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlacementDueDateOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placement_id", nullable = false)
    private Placement placement;

    @Column(name = "auto_calculated_due_date", nullable = false)
    private LocalDate autoCalculatedDueDate;

    @Column(name = "previous_applied_due_date", nullable = false)
    private LocalDate previousAppliedDueDate;

    @Column(name = "new_applied_due_date", nullable = false)
    private LocalDate newAppliedDueDate;

    @Column(name = "override_reason", nullable = false, columnDefinition = "TEXT")
    private String overrideReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Column(name = "changed_at")
    private Instant changedAt;
}
