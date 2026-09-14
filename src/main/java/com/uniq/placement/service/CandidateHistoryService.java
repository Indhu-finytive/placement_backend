package com.uniq.placement.service;

import com.uniq.placement.dto.candidate.CandidateHistoryResponseDto;
import com.uniq.placement.entity.CandidateHistory;
import com.uniq.placement.repository.CandidateHistoryRepository;
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
public class CandidateHistoryService {

    private final CandidateHistoryRepository candidateHistoryRepository;

    @Transactional
    public void log(UUID candidatesId, String userId, String actionType, String message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String actionBy = auth != null && auth.getName() != null ? auth.getName() : "system";

        CandidateHistory history = CandidateHistory.builder()
                .candidatesId(candidatesId)
                .userId(userId)
                .actionType(actionType)
                .actionBy(actionBy)
                .message(message)
                .actionAt(Instant.now())
                .build();
        candidateHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<CandidateHistoryResponseDto> getHistory(UUID candidatesId) {
        return candidateHistoryRepository.findByCandidatesIdOrderByActionAtDesc(candidatesId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CandidateHistoryResponseDto mapToDto(CandidateHistory item) {
        return CandidateHistoryResponseDto.builder()
                .historyId(item.getHistoryId())
                .candidatesId(item.getCandidatesId())
                .userId(item.getUserId())
                .actionType(item.getActionType())
                .actionBy(item.getActionBy())
                .message(item.getMessage())
                .actionAt(item.getActionAt())
                .build();
    }
}
