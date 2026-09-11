package com.uniq.placement.dto.team;

import com.uniq.placement.entity.enums.ActiveStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class TeamInputDto {
    @NotBlank(message = "Team name is required")
    private String name;

    @NotNull(message = "Branch is required")
    private UUID branchId;

    private ActiveStatus status;
}
