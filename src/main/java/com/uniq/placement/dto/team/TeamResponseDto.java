package com.uniq.placement.dto.team;

import com.uniq.placement.entity.enums.ActiveStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TeamResponseDto {
    private UUID id;
    private String name;
    private UUID branchId;
    private String branchName;
    private ActiveStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
