package com.odms.order.repository;

import com.odms.order.entity.DistanceRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistanceRangeRepository extends JpaRepository<DistanceRange, Integer> {
}
