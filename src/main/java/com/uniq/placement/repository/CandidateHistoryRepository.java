package com.uniq.placement.repository;

import com.uniq.placement.entity.CandidateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CandidateHistoryRepository extends JpaRepository<CandidateHistory, Long> {
    List<CandidateHistory> findByCandidatesIdOrderByActionAtDesc(UUID candidatesId);
}
