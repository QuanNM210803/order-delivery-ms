package com.odms.delivery.repository;

import com.odms.delivery.document.DeliveryOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryOrderRepository extends MongoRepository<DeliveryOrder, String> {
    Optional<DeliveryOrder> findByOrderCode(String orderCode);
}
