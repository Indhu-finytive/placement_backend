package com.uniq.placement.dto.audit;

import lombok.Data;

import java.time.Instant;

@Data
public class AuditEventDto {
    private String id;
    private Instant occurredAt;
    private String userId;
    private String userName;
    private String action;
    private String record;
}
