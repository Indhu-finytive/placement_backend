package com.uniq.placement.repository;

import com.uniq.placement.entity.ShareAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShareAllocationRepository extends JpaRepository<ShareAllocation, UUID> {

    Optional<ShareAllocation> findByPaymentId(UUID paymentId);

    @Query("SELECT sa.partner, SUM(sa.amount) FROM ShareAllocation sa GROUP BY sa.partner")
    List<Object[]> sumByPartner();
}
