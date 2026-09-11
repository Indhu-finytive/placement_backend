package com.uniq.placement.repository;

import com.uniq.placement.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {
    List<Branch> findByIsActive(Boolean isActive);
    List<Branch> findAllByOrderByNameAsc();
    boolean existsByCode(String code);
    boolean existsByName(String name);
    List<Branch> findByCodeStartingWithOrderByCodeAsc(String code);
}
