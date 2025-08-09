package com.odms.auth.repository;

import com.odms.auth.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("SELECT ur FROM UserRole ur " +
           "WHERE ur.user.id = :userId AND ur.role.id = :roleId AND ur.isDeleted = :isDeleted")
    Optional<UserRole> findByUserIdAndRoleId(Long userId, Long roleId, boolean isDeleted);
}
