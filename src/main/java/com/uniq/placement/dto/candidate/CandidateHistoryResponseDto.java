package com.uniq.placement.dto.candidate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateHistoryResponseDto {
    private Long historyId;
    private UUID candidatesId;
    private String userId;
    private String actionType;
    private String actionBy;
    private String message;
    private Instant actionAt;
}
