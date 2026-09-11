package com.uniq.placement.controller;

import com.uniq.placement.dto.candidate.CandidateCreateDto;
import com.uniq.placement.dto.candidate.CandidateResponseDto;
import com.uniq.placement.dto.candidate.CandidateUpdateDto;
import com.uniq.placement.dto.common.PageDto;
import com.uniq.placement.dto.placement.PlacementInputDto;
import com.uniq.placement.dto.placement.PlacementResponseDto;
import com.uniq.placement.entity.enums.CandidateStatus;
import com.uniq.placement.entity.enums.Eligibility;
import com.uniq.placement.service.CandidateService;
import com.uniq.placement.service.PlacementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;
    private final PlacementService placementService;

    @GetMapping
    public ResponseEntity<PageDto<CandidateResponseDto>> getCandidates(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String team,
            @RequestParam(required = false) CandidateStatus status,
            @RequestParam(required = false) Eligibility eligibility,
            @RequestParam(required = false) String course,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int pageSize) {
        return ResponseEntity.ok(candidateService.getCandidates(search, team, status, eligibility, course, page, pageSize));
    }

    @PostMapping
    public ResponseEntity<CandidateResponseDto> createCandidate(@Valid @RequestBody CandidateCreateDto request) {
        return new ResponseEntity<>(candidateService.createCandidate(request), HttpStatus.CREATED);
    }

    @GetMapping("/{candidateId}")
    public ResponseEntity<CandidateResponseDto> getCandidate(@PathVariable UUID candidateId) {
        return ResponseEntity.ok(candidateService.getCandidate(candidateId));
    }

    @PatchMapping("/{candidateId}")
    public ResponseEntity<CandidateResponseDto> updateCandidate(
            @PathVariable UUID candidateId,
            @Valid @RequestBody CandidateUpdateDto request) {
        return ResponseEntity.ok(candidateService.updateCandidate(candidateId, request));
    }

    @PutMapping("/{candidateId}/placement")
    public ResponseEntity<PlacementResponseDto> savePlacement(
            @PathVariable UUID candidateId,
            @Valid @RequestBody PlacementInputDto request) {
        return ResponseEntity.ok(placementService.savePlacement(candidateId, request));
    }
}
