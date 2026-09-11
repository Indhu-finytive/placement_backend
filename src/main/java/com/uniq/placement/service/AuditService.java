package com.uniq.placement.service;

import com.uniq.placement.dto.audit.AuditEventDto;
import com.uniq.placement.dto.common.PageDto;
import com.uniq.placement.entity.AuditLog;
import com.uniq.placement.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public PageDto<AuditEventDto> getAuditEvents(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<AuditLog> auditPage = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<AuditEventDto> dtos = auditPage.getContent().stream().map(log -> {
            AuditEventDto dto = new AuditEventDto();
            dto.setId(log.getId().toString());
            dto.setOccurredAt(log.getCreatedAt());
            dto.setUserId(log.getUser() != null ? log.getUser().getId().toString() : null);
            dto.setUserName(log.getUserName());
            dto.setAction(log.getAction());
            dto.setRecord(log.getEntityType() + " " + log.getEntityId());
            return dto;
        }).collect(Collectors.toList());

        return new PageDto<>(dtos, page, pageSize, auditPage.getTotalElements());
    }
}
