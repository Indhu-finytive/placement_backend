package com.uniq.placement.repository;

import com.uniq.placement.entity.Payment;
import com.uniq.placement.entity.enums.PaymentType;
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
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByCandidateIdOrderByPaymentDateDesc(UUID candidateId);

    Optional<Payment> findByPaymentCode(String paymentCode);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.candidate.id = :candidateId")
    int countByCandidateId(@Param("candidateId") UUID candidateId);

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.candidate LEFT JOIN FETCH p.accountHolder " +
           "LEFT JOIN FETCH p.receivedBy LEFT JOIN FETCH p.allocation WHERE " +
           "(:from IS NULL OR p.paymentDate >= :from) AND " +
           "(:to IS NULL OR p.paymentDate <= :to) AND " +
           "(:teamIds IS NULL OR p.candidate.assignedTeam.id IN :teamIds) AND " +
           "(:userId IS NULL OR p.receivedBy.id = :userId) AND " +
           "(:accountName IS NULL OR LOWER(p.accountName) = LOWER(CAST(:accountName AS string))) AND " +
           "(:paymentType IS NULL OR p.paymentType = :paymentType) AND " +
           "(:search IS NULL OR LOWER(p.candidate.candidateName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "OR LOWER(p.candidate.candidateCode) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))")
    List<Payment> findCollections(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("teamIds") Collection<UUID> teamIds,
            @Param("userId") UUID userId,
            @Param("accountName") String accountName,
            @Param("paymentType") PaymentType paymentType,
            @Param("search") String search);

    @Query("SELECT p FROM Payment p WHERE p.accountName = :accountName ORDER BY p.paymentDate DESC")
    List<Payment> findByAccountName(@Param("accountName") String accountName);

    @Query("SELECT p.accountName, COUNT(p), SUM(p.amount), MAX(p.paymentDate) FROM Payment p " +
           "GROUP BY p.accountName ORDER BY SUM(p.amount) DESC")
    List<Object[]> getAccountSummaries();

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(p.paymentCode, LENGTH(:prefix) + 2) AS int)), 100) " +
           "FROM Payment p WHERE p.paymentCode LIKE CONCAT(:prefix, '-%')")
    int findMaxSequenceByPrefix(@Param("prefix") String prefix);
}
