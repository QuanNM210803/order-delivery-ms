package com.odms.auth.repository;

import com.odms.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT r FROM Role r " +
            "WHERE r.name = :name AND r.isDeleted = :isDeleted")
    Optional<Role> findByName(String name, boolean isDeleted);

    @Query("SELECT r FROM Role r " +
            "JOIN UserRole ur ON ur.role.id = r.id AND ur.user.id = :userId AND ur.isDeleted = :isDeleted " +
            "WHERE r.isDeleted = :isDeleted")
    List<Role> findAllByUserId(Long userId, boolean isDeleted);
}
