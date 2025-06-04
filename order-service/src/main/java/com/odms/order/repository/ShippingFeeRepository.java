package com.odms.order.repository;

import com.odms.order.entity.ShippingFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingFeeRepository extends JpaRepository<ShippingFee, Integer> {
}
