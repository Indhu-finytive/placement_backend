package com.uniq.placement.controller;

import com.uniq.placement.dto.audit.AuditEventDto;
import com.uniq.placement.dto.common.PageDto;
import com.uniq.placement.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit-events")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<PageDto<AuditEventDto>> getAuditEvents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int pageSize) {
        return ResponseEntity.ok(auditService.getAuditEvents(page, pageSize));
    }
}
