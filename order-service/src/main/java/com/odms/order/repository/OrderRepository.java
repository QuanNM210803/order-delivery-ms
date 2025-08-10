package com.odms.order.repository;

import com.odms.order.dto.StatisticsDeliveryProjection;
import com.odms.order.entity.Order;
import com.odms.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.isDeleted = :isDeleted AND o.orderCode = :orderCode")
    Optional<Order> findByOrderCode(String orderCode, boolean isDeleted);

    @Query("SELECT o FROM Order o WHERE " +
            "o.isDeleted = :isDeleted AND " +
            "(:customerId IS NULL OR o.customerId = :customerId) AND " +
            "(:orderCode IS NULL OR o.orderCode LIKE %:orderCode%) AND " +
            "(:receiverName IS NULL OR LOWER(o.receiverName) LIKE %:receiverName%) AND " +
            "(:receiverPhone IS NULL OR LOWER(o.receiverPhone) LIKE %:receiverPhone%) AND " +
            "(:orderStatuses IS NULL OR o.orderStatus IN :orderStatuses) AND " +
            "(o.createdAt >= COALESCE(:startDate, o.createdAt)) AND " +
            "(o.createdAt <= COALESCE(:endDate, o.createdAt))")
    Page<Order> filterByCustomer(
            Long customerId,
            String orderCode,
            String receiverName,
            String receiverPhone,
            List<OrderStatus> orderStatuses,
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean isDeleted,
            Pageable pageable
    );

    @Query("SELECT o FROM Order o WHERE " +
            "o.isDeleted = :isDeleted AND " +
            "(:deliveryStaffId IS NULL OR o.deliveryStaffId = :deliveryStaffId) AND " +
            "(:orderCode IS NULL OR o.orderCode LIKE %:orderCode%) AND " +
            "(:senderName IS NULL OR LOWER(o.senderName) LIKE %:senderName%) AND " +
            "(:description IS NULL OR LOWER(o.description) LIKE %:description%) AND " +
            "(:orderStatuses IS NULL OR o.orderStatus IN :orderStatuses) AND " +
            "(o.createdAt >= COALESCE(:startDate, o.createdAt)) AND " +
            "(o.createdAt <= COALESCE(:endDate, o.createdAt))")
    Page<Order> filterByDelivery(
            Long deliveryStaffId,
            String orderCode,
            String senderName,
            String description,
            List<String> orderStatuses,
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean isDeleted,
            Pageable pageable
    );

    @Query("SELECT o FROM Order o WHERE " +
            "o.isDeleted = :isDeleted AND " +
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
            boolean isDeleted,
            Pageable pageable
    );

    @Query(value = """
        SELECT
            staff_stats.delivery_staff_id AS deliveryStaffId,
            staff_stats.order_completed AS orderCompleted,
            staff_stats.order_cancelled AS orderCancelled,
            staff_stats.order_pending AS orderPending,
            staff_stats.shipping_fee_total AS shippingFeeTotal,
            staff_stats.ranking AS ranking
        FROM (
            SELECT
                o.delivery_staff_id AS delivery_staff_id,
                SUM(CASE WHEN o.order_status = 'COMPLETED' THEN 1 ELSE 0 END) AS order_completed,
                SUM(CASE WHEN o.order_status = 'CANCELLED' THEN 1 ELSE 0 END) AS order_cancelled,
                SUM(CASE WHEN (o.order_status = 'ASSIGNED' OR o.order_status = 'PICKED_UP' OR o.order_status = 'IN_TRANSIT' OR o.order_status = 'DELIVERED') THEN 1 ELSE 0 END) AS order_pending,
                SUM(CASE WHEN o.order_status = 'COMPLETED' THEN o.shipping_fee ELSE 0 END) AS shipping_fee_total,
                RANK() OVER (ORDER BY SUM(CASE WHEN o.order_status = 'COMPLETED' THEN o.shipping_fee ELSE 0 END) DESC) AS ranking
            FROM orders o
            WHERE o.created_at BETWEEN COALESCE(:startDate, o.created_at) AND COALESCE(:endDate, o.created_at)
            GROUP BY o.delivery_staff_id
        ) staff_stats
        WHERE staff_stats.delivery_staff_id = :deliveryStaffId
        """, nativeQuery = true)
    Optional<StatisticsDeliveryProjection> getStatisticsForDeliveryStaff(
            @Param("deliveryStaffId") Long deliveryStaffId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
