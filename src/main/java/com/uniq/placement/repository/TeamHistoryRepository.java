package com.uniq.placement.repository;

import com.uniq.placement.entity.TeamHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamHistoryRepository extends JpaRepository<TeamHistory, Long> {
    List<TeamHistory> findByTeamIdOrderByActionAtDesc(UUID teamId);
}
