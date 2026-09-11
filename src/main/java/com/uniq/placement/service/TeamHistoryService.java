package com.uniq.placement.service;

import com.uniq.placement.dto.team.TeamHistoryResponseDto;
import com.uniq.placement.entity.TeamHistory;
import com.uniq.placement.repository.TeamHistoryRepository;
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
public class TeamHistoryService {

    private final TeamHistoryRepository teamHistoryRepository;

    @Transactional
    public void log(UUID teamId, String userId, String actionType, String message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String actionBy = auth != null && auth.getName() != null ? auth.getName() : "system";

        TeamHistory history = TeamHistory.builder()
                .teamId(teamId)
                .userId(userId)
                .actionType(actionType)
                .actionBy(actionBy)
                .message(message)
                .actionAt(Instant.now())
                .build();
        teamHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<TeamHistoryResponseDto> getHistory(UUID teamId) {
        return teamHistoryRepository.findByTeamIdOrderByActionAtDesc(teamId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private TeamHistoryResponseDto mapToDto(TeamHistory item) {
        TeamHistoryResponseDto dto = new TeamHistoryResponseDto();
        dto.setHistoryId(item.getHistoryId());
        dto.setTeamId(item.getTeamId());
        dto.setUserId(item.getUserId());
        dto.setActionType(item.getActionType());
        dto.setActionBy(item.getActionBy());
        dto.setMessage(item.getMessage());
        dto.setActionAt(item.getActionAt());
        return dto;
    }
}
