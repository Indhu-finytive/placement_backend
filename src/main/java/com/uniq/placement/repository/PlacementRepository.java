package com.uniq.placement.repository;

import com.uniq.placement.entity.Placement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlacementRepository extends JpaRepository<Placement, UUID> {

    Optional<Placement> findByCandidateId(UUID candidateId);

    @Query("SELECT p FROM Placement p WHERE " +
           "(:from IS NULL OR p.placementDate >= :from) AND " +
           "(:to IS NULL OR p.placementDate <= :to) AND " +
           "(:teamIds IS NULL OR p.candidate.assignedTeam.id IN :teamIds)")
    List<Placement> findByFilters(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("teamIds") Collection<UUID> teamIds);
}
