package com.odms.auth.repository;

import com.odms.auth.entity.DeliveryStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryStaffRepository extends JpaRepository<DeliveryStaff, Integer> {

    @Query("SELECT ds FROM DeliveryStaff ds WHERE ds.user.userId = :userId")
    Optional<DeliveryStaff> findByUserId(Integer userId);

    @Query("SELECT ds FROM DeliveryStaff ds WHERE ds.findingOrder = :status")
    List<DeliveryStaff> findByFindingOrder(Boolean status);
}
