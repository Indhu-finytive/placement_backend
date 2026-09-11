package com.uniq.placement.entity;

import com.uniq.placement.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "placements")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Placement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false, unique = true)
    private Candidate candidate;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "job_role", length = 150)
    private String jobRole;

    @Column(name = "company_location", length = 150)
    private String companyLocation;

    @Column(name = "placement_date", nullable = false)
    private LocalDate placementDate;

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    @Column(name = "due_period_days", nullable = false)
    private Integer duePeriodDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "due_period_label")
    private DuePeriod duePeriodLabel;

    @Column(name = "auto_calculated_due_date", nullable = false)
    private LocalDate autoCalculatedDueDate;

    @Column(name = "final_applied_due_date", nullable = false)
    private LocalDate finalAppliedDueDate;

    @Column(name = "annual_ctc", nullable = false, precision = 14, scale = 2)
    private BigDecimal annualCtc;

    @Column(name = "committed_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal committedPercentage;

    @Column(name = "committed_amount", precision = 14, scale = 2)
    private BigDecimal committedAmount;

    @Column(name = "payment_terms", columnDefinition = "TEXT")
    private String paymentTerms;

    @Enumerated(EnumType.STRING)
    @Column(name = "referral_type")
    private ReferralType referralType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_team_id")
    private Team shareTeam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlacementStatusEnum status;

    @Column(name = "offer_letter_url")
    private String offerLetterUrl;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
