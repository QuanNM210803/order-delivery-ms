package com.odms.auth.repository;

import com.odms.auth.dto.RoleName;
import com.odms.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllByUserIdIn(List<Integer> userIds);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN u.roles r " +
            "WHERE (:username IS NULL OR u.username LIKE %:username%) " +
            "AND (:fullName IS NULL OR LOWER(u.fullName) LIKE %:fullName%) " +
            "AND (:phone IS NULL OR u.phone LIKE %:phone%) " +
            "AND (:roleNames IS NULL OR r.name IN :roleNames)")
    Page<User> filterUsers(
            String username,
            String fullName,
            String phone,
            List<String> roleNames,
            Pageable pageable
    );
}
