package com.uniq.placement.repository;

import com.uniq.placement.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    List<Team> findByIsActive(Boolean isActive);
    List<Team> findAllByOrderByNameAsc();
    boolean existsByName(String name);
}
