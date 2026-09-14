package com.uniq.placement.entity;

import com.uniq.placement.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "account_holders")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Convert(converter = LedgerTypeConverter.class)
    @Column(name = "linked_ledger_type", nullable = false)
    private LedgerType linkedLedgerType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_team_id")
    private Team linkedTeam;

    @Column(length = 150)
    private String bank;

    @Column(length = 4)
    private String last4;

    @Column(length = 150)
    private String upi;

    @Column(name = "payment_type", length = 50)
    private String paymentType;

    @Convert(converter = ActiveStatusConverter.class)
    @Column(nullable = false)
    private ActiveStatus status;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
