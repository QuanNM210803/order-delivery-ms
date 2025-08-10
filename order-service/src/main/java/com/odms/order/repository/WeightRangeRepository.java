package com.odms.order.repository;

import com.odms.order.entity.WeightRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WeightRangeRepository extends JpaRepository<WeightRange, Long> {

    @Query("SELECT w FROM WeightRange w WHERE w.isDeleted = :isDeleted " +
            "AND w.fromGam < :weight " +
            "AND (w.toGam IS NULL OR w.toGam >= :weight)")
    WeightRange findByWeightRange(Double weight, boolean isDeleted);
}
