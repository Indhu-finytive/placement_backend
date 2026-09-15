package com.uniq.placement.repository;

import com.uniq.placement.entity.User;
import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.entity.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = {"teams"})
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query(value = "SELECT DISTINCT u FROM User u LEFT JOIN u.teams t WHERE " +
           "(:search IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "OR LOWER(u.username) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "OR LOWER(u.mobileNumber) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) " +
           "AND (:role IS NULL OR u.role = :role) " +
           "AND (:teamId IS NULL OR t.id = :teamId) " +
           "AND (:isActive IS NULL OR u.isActive = :isActive)",
           countQuery = "SELECT COUNT(DISTINCT u) FROM User u LEFT JOIN u.teams t WHERE " +
           "(:search IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "OR LOWER(u.username) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "OR LOWER(u.mobileNumber) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) " +
           "AND (:role IS NULL OR u.role = :role) " +
           "AND (:teamId IS NULL OR t.id = :teamId) " +
           "AND (:isActive IS NULL OR u.isActive = :isActive)")
    Page<User> findAllWithFilters(
            @Param("search") String search,
            @Param("role") UserRole role,
            @Param("teamId") UUID teamId,
            @Param("isActive") Boolean isActive,
            Pageable pageable);
}
