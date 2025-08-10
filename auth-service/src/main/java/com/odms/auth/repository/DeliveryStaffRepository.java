package com.odms.auth.repository;

import com.odms.auth.entity.DeliveryStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryStaffRepository extends JpaRepository<DeliveryStaff, Long> {

    @Query("SELECT ds FROM DeliveryStaff ds WHERE ds.user.id = :userId AND ds.isDeleted = :isDeleted")
    Optional<DeliveryStaff> findByUserId(Long userId, boolean isDeleted);

    @Query("SELECT ds FROM DeliveryStaff ds WHERE ds.findingOrder = :status AND ds.isDeleted = :isDeleted")
    List<DeliveryStaff> findByFindingOrder(Boolean status, boolean isDeleted);
}
