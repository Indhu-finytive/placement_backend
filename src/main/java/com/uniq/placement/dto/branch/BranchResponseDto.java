package com.uniq.placement.dto.branch;

import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.entity.enums.BranchLocation;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class BranchResponseDto {
    private UUID id;
    private String name;
    private String code;
    private BranchLocation location;
    private ActiveStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
