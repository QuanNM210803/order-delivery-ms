package com.odms.order.repository;

import com.odms.order.entity.WeightRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeightRangeRepository extends JpaRepository<WeightRange, Integer> {
}
