package com.uniq.placement.dto.user;

import com.uniq.placement.entity.enums.ActiveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserStatusUpdateDto {
    @NotNull(message = "Status is required")
    private ActiveStatus status;
}
