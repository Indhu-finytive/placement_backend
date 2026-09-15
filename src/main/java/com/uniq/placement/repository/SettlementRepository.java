package com.uniq.placement.repository;

import com.uniq.placement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, UUID> {

    @Query("SELECT s FROM Settlement s WHERE " +
           "(:partner IS NULL OR LOWER(s.partner) = LOWER(CAST(:partner AS string))) AND " +
           "(:from IS NULL OR s.settlementDate >= :from) AND " +
           "(:to IS NULL OR s.settlementDate <= :to) " +
           "ORDER BY s.settlementDate DESC")
    List<Settlement> findByFilters(
            @Param("partner") String partner,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Query("SELECT s.partner, s.direction, SUM(s.amount) FROM Settlement s GROUP BY s.partner, s.direction")
    List<Object[]> sumByPartnerAndDirection();
}
