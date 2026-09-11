package com.uniq.placement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "team_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TeamHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "action_type", length = 50)
    private String actionType;

    @Column(name = "action_by", length = 50)
    private String actionBy;

    @Column(name = "action_at")
    private Instant actionAt;
}
