package com.uniq.placement.entity;

import com.uniq.placement.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "candidates")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "candidate_code", nullable = false, unique = true, length = 30)
    private String candidateCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_team_id")
    private Team assignedTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(name = "candidate_name", nullable = false, length = 150)
    private String candidateName;

    @Column(name = "mobile_number", nullable = false, unique = true, length = 15)
    private String mobileNumber;

    @Column(name = "alternate_mobile", length = 15)
    private String alternateMobile;

    @Column(length = 150)
    private String email;

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @Column(length = 100)
    private String qualification;

    @Column(length = 100)
    private String degree;

    @Column(length = 100)
    private String department;

    @Column(name = "passing_year")
    private Short passingYear;

    @Column(name = "college_name", length = 200)
    private String collegeName;

    @Column(name = "current_location", length = 150)
    private String currentLocation;

    @Column(length = 100)
    private String course;

    @Convert(converter = TrainingModeConverter.class)
    @Column(name = "batch_type")
    private TrainingMode batchType;

    @Column(name = "branch", length = 100, insertable = false, updatable = false)
    private String branchName;

    @Column(length = 150)
    private String trainer;

    @Convert(converter = CandidateStatusConverter.class)
    @Column(nullable = false)
    private CandidateStatus status;

    @Convert(converter = EligibilityConverter.class)
    private Eligibility eligibility;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @OneToOne(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Placement placement;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
