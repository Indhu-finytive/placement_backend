package com.uniq.placement.service;

import com.uniq.placement.dto.team.TeamInputDto;
import com.uniq.placement.dto.team.TeamResponseDto;
import com.uniq.placement.entity.Branch;
import com.uniq.placement.entity.Team;
import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.exception.DuplicateResourceException;
import com.uniq.placement.exception.ResourceNotFoundException;
import com.uniq.placement.repository.BranchRepository;
import com.uniq.placement.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final BranchRepository branchRepository;
    private final TeamHistoryService teamHistoryService;

    @Transactional(readOnly = true)
    public List<TeamResponseDto> getTeams(ActiveStatus status) {
        List<Team> teams = status == null
                ? teamRepository.findAllByOrderByNameAsc()
                : teamRepository.findByIsActive(status == ActiveStatus.ACTIVE);
        return teams.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public TeamResponseDto createTeam(TeamInputDto dto) {
        if (teamRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Team name already exists");
        }
        Branch branch = branchRepository.findById(dto.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        Team team = Team.builder()
                .name(dto.getName())
                .branch(branch)
                .isActive(dto.getStatus() == null || dto.getStatus() == ActiveStatus.ACTIVE)
                .build();

        Team saved = teamRepository.save(team);
        writeHistory(saved.getId(), "CREATE", "Team created as " + saved.getName() + " under " + branch.getName());
        return mapToDto(saved);
    }

    @Transactional
    public TeamResponseDto updateTeam(UUID id, TeamInputDto dto) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        if (dto.getName() != null && !dto.getName().equals(team.getName()) && teamRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Team name already exists");
        }

        String previousName = team.getName();
        UUID previousBranchId = team.getBranch() != null ? team.getBranch().getId() : null;
        boolean previousActive = team.getIsActive();

        if (dto.getName() != null) team.setName(dto.getName());
        if (dto.getBranchId() != null) {
            Branch branch = branchRepository.findById(dto.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
            team.setBranch(branch);
        }
        if (dto.getStatus() != null) team.setIsActive(dto.getStatus() == ActiveStatus.ACTIVE);

        Team saved = teamRepository.save(team);
        String historyMessage = buildUpdateHistoryMessage(previousName, previousBranchId,
                previousActive, saved.getName(), saved.getBranch() != null ? saved.getBranch().getId() : null,
                saved.getIsActive());
        if (historyMessage != null && !historyMessage.isBlank()) {
            writeHistory(saved.getId(), "UPDATE", historyMessage);
        }
        return mapToDto(saved);
    }

    @Transactional
    public TeamResponseDto toggleTeam(UUID id, ActiveStatus status) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        boolean wasActive = team.getIsActive();
        team.setIsActive(status == ActiveStatus.ACTIVE);
        Team saved = teamRepository.save(team);
        writeHistory(saved.getId(), "STATUS", "Team status changed from " + (wasActive ? "Active" : "Inactive") + " to " + (saved.getIsActive() ? "Active" : "Inactive"));
        return mapToDto(saved);
    }

    private String buildUpdateHistoryMessage(String previousName, UUID previousBranchId,
                                              boolean previousActive, String newName,
                                              UUID newBranchId, boolean newActive) {
        StringBuilder message = new StringBuilder();

        if (previousName != null && !previousName.equals(newName)) {
            if (message.length() > 0) message.append("; ");
            message.append("Team name changed from ").append(previousName).append(" to ").append(newName);
        }

        if (previousBranchId != null && !previousBranchId.equals(newBranchId)) {
            if (message.length() > 0) message.append("; ");
            Branch previousBranch = branchRepository.findById(previousBranchId).orElse(null);
            Branch newBranch = branchRepository.findById(newBranchId).orElse(null);
            message.append("Branch changed from ")
                    .append(previousBranch != null ? previousBranch.getName() : "Unknown")
                    .append(" to ")
                    .append(newBranch != null ? newBranch.getName() : "Unknown");
        }

        if (previousActive != newActive) {
            if (message.length() > 0) message.append("; ");
            message.append("Status changed from ")
                    .append(previousActive ? "Active" : "Inactive")
                    .append(" to ")
                    .append(newActive ? "Active" : "Inactive");
        }

        return message.length() == 0 ? null : message.toString();
    }

    private TeamResponseDto mapToDto(Team team) {
        TeamResponseDto dto = new TeamResponseDto();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setBranchId(team.getBranch() != null ? team.getBranch().getId() : null);
        dto.setBranchName(team.getBranch() != null ? team.getBranch().getName() : null);
        dto.setStatus(team.getIsActive() ? ActiveStatus.ACTIVE : ActiveStatus.INACTIVE);
        dto.setCreatedAt(team.getCreatedAt());
        dto.setUpdatedAt(team.getUpdatedAt());
        return dto;
    }

    private void writeHistory(UUID teamId, String actionType, String message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth != null && auth.getName() != null ? auth.getName() : "system";
        teamHistoryService.log(teamId, userName, actionType, message);
    }
}
