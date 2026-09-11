package com.uniq.placement.repository;

import com.uniq.placement.entity.AccountHolder;
import com.uniq.placement.entity.enums.ActiveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, UUID> {

    List<AccountHolder> findByStatus(ActiveStatus status);
}
