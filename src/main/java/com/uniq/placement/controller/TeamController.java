package com.uniq.placement.controller;

import com.uniq.placement.dto.team.TeamHistoryResponseDto;
import com.uniq.placement.dto.team.TeamInputDto;
import com.uniq.placement.dto.team.TeamResponseDto;
import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.service.TeamHistoryService;
import com.uniq.placement.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final TeamHistoryService teamHistoryService;

    @GetMapping
    public ResponseEntity<List<TeamResponseDto>> getTeams(
            @RequestParam(required = false) ActiveStatus status) {
        return ResponseEntity.ok(teamService.getTeams(status));
    }

    @GetMapping("/{teamId}/history")
    public ResponseEntity<List<TeamHistoryResponseDto>> getTeamHistory(@PathVariable UUID teamId) {
        return ResponseEntity.ok(teamHistoryService.getHistory(teamId));
    }

    @PostMapping
    public ResponseEntity<TeamResponseDto> createTeam(@Valid @RequestBody TeamInputDto request) {
        return new ResponseEntity<>(teamService.createTeam(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{teamId}")
    public ResponseEntity<TeamResponseDto> updateTeam(
            @PathVariable UUID teamId,
            @Valid @RequestBody TeamInputDto request) {
        return ResponseEntity.ok(teamService.updateTeam(teamId, request));
    }

    @PatchMapping("/{teamId}/status")
    public ResponseEntity<TeamResponseDto> updateStatus(
            @PathVariable UUID teamId,
            @RequestParam ActiveStatus status) {
        return ResponseEntity.ok(teamService.toggleTeam(teamId, status));
    }
}
