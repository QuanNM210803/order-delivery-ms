package com.odms.order.repository;

import com.odms.order.entity.Order;
import com.odms.order.entity.enumerate.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByOrderCode(String orderCode);

    @Query("SELECT o FROM Order o WHERE " +
            "(:customerId IS NULL OR o.customerId = :customerId) AND " +
            "(:orderCode IS NULL OR o.orderCode LIKE %:orderCode%) AND " +
            "(:receiverName IS NULL OR LOWER(o.receiverName) LIKE %:receiverName%) AND " +
            "(:receiverPhone IS NULL OR LOWER(o.receiverPhone) LIKE %:receiverPhone%) AND " +
            "(:orderStatuses IS NULL OR o.orderStatus IN :orderStatuses) AND " +
            "(o.createdAt >= COALESCE(:startDate, o.createdAt)) AND " +
            "(o.createdAt <= COALESCE(:endDate, o.createdAt))")
    Page<Order> filterByCustomer(
            Integer customerId,
            String orderCode,
            String receiverName,
            String receiverPhone,
            List<OrderStatus> orderStatuses,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT o FROM Order o WHERE " +
            "(:deliveryStaffId IS NULL OR o.deliveryStaffId = :deliveryStaffId) AND " +
            "(:orderCode IS NULL OR o.orderCode LIKE %:orderCode%) AND " +
            "(:senderName IS NULL OR LOWER(o.senderName) LIKE %:senderName%) AND " +
            "(:description IS NULL OR LOWER(o.description) LIKE %:description%) AND " +
            "(:orderStatuses IS NULL OR o.orderStatus IN :orderStatuses) AND " +
            "(o.createdAt >= COALESCE(:startDate, o.createdAt)) AND " +
            "(o.createdAt <= COALESCE(:endDate, o.createdAt))")
    Page<Order> filterByDelivery(
            Integer deliveryStaffId,
            String orderCode,
            String senderName,
            String description,
            List<OrderStatus> orderStatuses,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT o FROM Order o WHERE " +
            "(:orderCode IS NULL OR o.orderCode LIKE %:orderCode%) AND " +
            "(:senderName IS NULL OR LOWER(o.senderName) LIKE %:senderName%) AND " +
            "(:description IS NULL OR LOWER(o.description) LIKE %:description%) AND " +
            "(:orderStatuses IS NULL OR o.orderStatus IN :orderStatuses) AND " +
            "(o.createdAt >= COALESCE(:startDate, o.createdAt)) AND " +
            "(o.createdAt <= COALESCE(:endDate, o.createdAt))")
    Page<Order> filterByAdmin(
            String orderCode,
            String senderName,
            String description,
            List<OrderStatus> orderStatuses,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );
}
