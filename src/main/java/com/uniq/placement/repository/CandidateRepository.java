package com.uniq.placement.repository;

import com.uniq.placement.entity.Candidate;
import com.uniq.placement.entity.enums.CandidateStatus;
import com.uniq.placement.entity.enums.Eligibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, UUID> {

    Optional<Candidate> findByCandidateCode(String candidateCode);

    boolean existsByMobileNumber(String mobileNumber);

    @Query("SELECT c FROM Candidate c WHERE " +
           "(:search IS NULL OR LOWER(c.candidateName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "OR LOWER(c.mobileNumber) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "OR LOWER(c.candidateCode) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) " +
           "AND (:teamId IS NULL OR c.assignedTeam.id = :teamId) " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:eligibility IS NULL OR c.eligibility = :eligibility) " +
           "AND (:course IS NULL OR LOWER(c.course) = LOWER(CAST(:course AS string)))")
    Page<Candidate> findAllWithFilters(
            @Param("search") String search,
            @Param("teamId") UUID teamId,
            @Param("status") CandidateStatus status,
            @Param("eligibility") Eligibility eligibility,
            @Param("course") String course,
            Pageable pageable);

    @Query("SELECT c FROM Candidate c WHERE " +
           "(:search IS NULL OR LOWER(c.candidateName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "OR LOWER(c.mobileNumber) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "OR LOWER(c.candidateCode) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) " +
           "AND (:teamId IS NULL OR c.assignedTeam.id = :teamId) " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:eligibility IS NULL OR c.eligibility = :eligibility) " +
           "AND (:course IS NULL OR LOWER(c.course) = LOWER(CAST(:course AS string))) " +
           "AND c.assignedTeam.id IN :teamIds")
    Page<Candidate> findAllWithFilters(
            @Param("search") String search,
            @Param("teamId") UUID teamId,
            @Param("status") CandidateStatus status,
            @Param("eligibility") Eligibility eligibility,
            @Param("course") String course,
            @Param("teamIds") Collection<UUID> teamIds,
            Pageable pageable);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(c.candidateCode, LENGTH(:prefix) + 2) AS int)), 0) " +
           "FROM Candidate c WHERE c.candidateCode LIKE CONCAT(:prefix, '-%')")
    int findMaxSequenceByPrefix(@Param("prefix") String prefix);

    @Query("SELECT c FROM Candidate c LEFT JOIN FETCH c.placement LEFT JOIN FETCH c.payments " +
           "WHERE c.placement IS NOT NULL AND c.assignedTeam.id IN :teamIds")
    java.util.List<Candidate> findPlacedCandidatesByTeams(@Param("teamIds") Collection<UUID> teamIds);

    @Query("SELECT c FROM Candidate c LEFT JOIN FETCH c.placement LEFT JOIN FETCH c.payments " +
           "WHERE c.placement IS NOT NULL")
    java.util.List<Candidate> findAllPlacedCandidates();
}
