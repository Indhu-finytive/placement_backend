package com.uniq.placement.dto.branch;

import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.entity.enums.BranchLocation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BranchInputDto {
    @NotBlank(message = "Branch name is required")
    private String name;

    @NotNull(message = "Location is required")
    private BranchLocation location;

    private ActiveStatus status;
}
