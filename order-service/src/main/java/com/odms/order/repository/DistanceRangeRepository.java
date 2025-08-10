package com.odms.order.repository;

import com.odms.order.entity.DistanceRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DistanceRangeRepository extends JpaRepository<DistanceRange, Long> {

    @Query("SELECT d FROM DistanceRange d WHERE d.isDeleted = :isDeleted " +
            "AND d.fromM < :distance " +
            "AND (d.toM IS NULL OR d.toM >= :distance)")
    DistanceRange findByDistanceRange(Double distance, boolean isDeleted);
}
