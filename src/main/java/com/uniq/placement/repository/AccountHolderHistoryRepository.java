package com.uniq.placement.repository;

import com.uniq.placement.entity.AccountHolderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountHolderHistoryRepository extends JpaRepository<AccountHolderHistory, Long> {
}
