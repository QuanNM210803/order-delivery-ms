package com.odms.auth.repository;

import com.odms.auth.dto.UserDto;
import com.odms.auth.dto.request.FilterUserRequest;
import com.odms.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u " +
            "WHERE u.id = :userId AND u.isDeleted = :isDeleted")
    Optional<User> findById(Long userId, boolean isDeleted);

    @Query("SELECT u FROM User u " +
            "WHERE u.username = :username AND u.isDeleted = :isDeleted")
    Optional<User> findByUsername(String username, boolean isDeleted);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u " +
            "WHERE u.id IN (:userIds) AND u.isDeleted = :isDeleted")
    List<User> findAllByIdIn(List<Long> userIds, boolean isDeleted);

    @Query("SELECT ur.user.id AS id, ur.user.isActive AS isActive, ur.user.createdAt AS createdAt, ur.user.username AS username, " +
            "ur.user.fullName AS fullname, ur.user.email AS email, ur.user.address AS address, ur.role.id AS roleId, ur.role.name AS roleName " +
            "FROM UserRole ur " +
            "WHERE ur.isDeleted = false AND ur.user.isDeleted = false AND ur.role.isDeleted = false " +
            "AND (:#{#request.username} IS NULL OR LOWER(ur.user.username) LIKE LOWER(CONCAT('%', :#{#request.username}, '%'))) " +
            "AND (:#{#request.fullName} IS NULL OR LOWER(ur.user.fullName) LIKE CONCAT('%', :#{#request.fullName}, '%')) " +
            "AND (:#{#request.phone} IS NULL OR LOWER(ur.user.phone) LIKE CONCAT('%', :#{#request.phone}, '%')) " +
            "AND (:#{#request.active} IS NULL OR ur.user.isActive = :#{#request.active} ) " +
            "AND (:#{#request.roleIds} IS NULL OR " +
            "EXISTS (SELECT 1 FROM UserRole tmp WHERE tmp.isDeleted = false AND tmp.user.id = ur.user.id AND tmp.role.id IN :#{#request.roleIds}))")
    List<UserDto> filterUsers(@Param("request") FilterUserRequest request);
}
