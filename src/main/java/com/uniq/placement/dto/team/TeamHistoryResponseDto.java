package com.uniq.placement.dto.team;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TeamHistoryResponseDto {
    private Long historyId;
    private UUID teamId;
    private String userId;
    private String actionType;
    private String actionBy;
    private String message;
    private Instant actionAt;
}
