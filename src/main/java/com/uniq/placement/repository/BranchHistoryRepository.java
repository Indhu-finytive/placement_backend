package com.uniq.placement.repository;

import com.uniq.placement.entity.BranchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BranchHistoryRepository extends JpaRepository<BranchHistory, Long> {
    List<BranchHistory> findByBranchIdOrderByActionAtDesc(UUID branchId);
}
