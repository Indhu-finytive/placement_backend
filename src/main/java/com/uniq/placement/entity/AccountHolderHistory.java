package com.uniq.placement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "account_holders_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountHolderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "account_holders_id", nullable = false)
    private UUID accountHoldersId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "action_type", length = 50)
    private String actionType;

    @Column(name = "action_by", length = 50)
    private String actionBy;

    @Column(name = "action_at", nullable = false, updatable = false)
    private Instant actionAt;

    @PrePersist
    public void prePersist() {
        if (actionAt == null) {
            actionAt = Instant.now();
        }
    }
}
