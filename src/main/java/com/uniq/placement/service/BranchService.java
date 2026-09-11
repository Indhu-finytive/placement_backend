package com.uniq.placement.service;

import com.uniq.placement.dto.branch.BranchInputDto;
import com.uniq.placement.dto.branch.BranchResponseDto;
import com.uniq.placement.entity.Branch;
import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.entity.enums.BranchLocation;
import com.uniq.placement.exception.DuplicateResourceException;
import com.uniq.placement.exception.ResourceNotFoundException;
import com.uniq.placement.repository.BranchRepository;
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
public class BranchService {

    private final BranchRepository branchRepository;
    private final BranchHistoryService branchHistoryService;

    @Transactional(readOnly = true)
    public List<BranchResponseDto> getBranches(ActiveStatus status) {
        List<Branch> branches = status == null
                ? branchRepository.findAllByOrderByNameAsc()
                : branchRepository.findByIsActive(status == ActiveStatus.ACTIVE);
        return branches.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public BranchResponseDto createBranch(BranchInputDto dto) {
        if (branchRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Branch name already exists");
        }

        String code = generateCode(dto.getLocation());
        Branch branch = Branch.builder()
                .name(dto.getName())
                .code(code)
                .location(dto.getLocation())
                .isActive(dto.getStatus() == null || dto.getStatus() == ActiveStatus.ACTIVE)
                .build();

        Branch saved = branchRepository.save(branch);
        writeHistory(saved.getId(), "CREATE", "Branch created as " + saved.getCode());
        return mapToDto(saved);
    }

    @Transactional
    public BranchResponseDto updateBranch(UUID id, BranchInputDto dto) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        if (dto.getName() != null && !dto.getName().equals(branch.getName()) && branchRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Branch name already exists");
        }

        String previousName = branch.getName();
        BranchLocation previousLoc = branch.getLocation();
        boolean previousActive = branch.getIsActive();

        if (dto.getName() != null) branch.setName(dto.getName());
        if (dto.getLocation() != null) branch.setLocation(dto.getLocation());
        if (dto.getStatus() != null) branch.setIsActive(dto.getStatus() == ActiveStatus.ACTIVE);

        Branch saved = branchRepository.save(branch);
        String historyMessage = buildUpdateHistoryMessage(previousName, previousLoc, previousActive,
                saved.getName(), saved.getLocation(), saved.getIsActive());
        if (historyMessage != null && !historyMessage.isBlank()) {
            writeHistory(saved.getId(), "UPDATE", historyMessage);
        }
        return mapToDto(saved);
    }

    @Transactional
    public BranchResponseDto toggleBranch(UUID id, ActiveStatus status) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        boolean wasActive = branch.getIsActive();
        branch.setIsActive(status == ActiveStatus.ACTIVE);
        Branch saved = branchRepository.save(branch);
        writeHistory(saved.getId(), "STATUS", "Branch status changed from " + (wasActive ? "Active" : "Inactive") + " to " + (saved.getIsActive() ? "Active" : "Inactive"));
        return mapToDto(saved);
    }

    private String buildUpdateHistoryMessage(String previousName, BranchLocation previousLocation,
                                              boolean previousActive, String newName,
                                              BranchLocation newLocation, boolean newActive) {
        StringBuilder message = new StringBuilder();

        if (previousName != null && !previousName.equals(newName)) {
            if (message.length() > 0) message.append("; ");
            message.append("Branch name changed from ").append(previousName).append(" to ").append(newName);
        }

        if (previousLocation != newLocation) {
            if (message.length() > 0) message.append("; ");
            message.append("Location changed from ")
                    .append(displayLocation(previousLocation))
                    .append(" to ")
                    .append(displayLocation(newLocation));
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

    private String displayLocation(BranchLocation location) {
        if (location == null) return "Unknown";
        return location.getValue();
    }

    private String generateCode(BranchLocation location) {
        String prefix = switch (location) {
            case CHENNAI -> "CN";
            case BANGALORE -> "BL";
            case COIMBATORE -> "CO";
            case MADURAI -> "MD";
            case PONDICHERRY -> "PY";
            case SALEM -> "SA";
            case TIRUNELVELI -> "TV";
            case TRICHY -> "TR";
        };
        int sequence = 1;
        List<Branch> same = branchRepository.findByCodeStartingWithOrderByCodeAsc(prefix);
        if (!same.isEmpty()) {
            sequence = same.stream()
                    .map(Branch::getCode)
                    .filter(code -> code != null && code.startsWith(prefix))
                    .map(code -> code.replace(prefix, ""))
                    .filter(part -> part.matches("\\d+"))
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0) + 1;
        }
        return prefix + String.format("%03d", sequence);
    }

    private BranchResponseDto mapToDto(Branch branch) {
        BranchResponseDto dto = new BranchResponseDto();
        dto.setId(branch.getId());
        dto.setName(branch.getName());
        dto.setCode(branch.getCode());
        dto.setLocation(branch.getLocation());
        dto.setStatus(branch.getIsActive() ? ActiveStatus.ACTIVE : ActiveStatus.INACTIVE);
        dto.setCreatedAt(branch.getCreatedAt());
        dto.setUpdatedAt(branch.getUpdatedAt());
        return dto;
    }

    private void writeHistory(UUID branchId, String actionType, String message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth != null && auth.getName() != null ? auth.getName() : "system";
        branchHistoryService.log(branchId, userName, actionType, message);
    }
}
