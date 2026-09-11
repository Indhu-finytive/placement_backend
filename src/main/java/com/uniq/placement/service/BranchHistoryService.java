package com.uniq.placement.service;

import com.uniq.placement.dto.branch.BranchHistoryResponseDto;
import com.uniq.placement.entity.BranchHistory;
import com.uniq.placement.repository.BranchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchHistoryService {

    private final BranchHistoryRepository branchHistoryRepository;

    @Transactional
    public void log(UUID branchId, String userId, String actionType, String message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String actionBy = auth != null && auth.getName() != null ? auth.getName() : "system";

        BranchHistory history = BranchHistory.builder()
                .branchId(branchId)
                .userId(userId)
                .actionType(actionType)
                .actionBy(actionBy)
                .message(message)
                .actionAt(Instant.now())
                .build();
        branchHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<BranchHistoryResponseDto> getHistory(UUID branchId) {
        return branchHistoryRepository.findByBranchIdOrderByActionAtDesc(branchId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private BranchHistoryResponseDto mapToDto(BranchHistory item) {
        return BranchHistoryResponseDto.builder()
                .historyId(item.getHistoryId())
                .branchId(item.getBranchId())
                .userId(item.getUserId())
                .actionType(item.getActionType())
                .actionBy(item.getActionBy())
                .message(item.getMessage())
                .actionAt(item.getActionAt())
                .build();
    }
}
