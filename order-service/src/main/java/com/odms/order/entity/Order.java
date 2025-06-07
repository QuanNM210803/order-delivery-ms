package com.odms.order.entity;

import com.odms.order.entity.enumerate.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Order extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false, unique = true)
    private Integer orderId;

    @Column(name = "order_code", nullable = false, unique = true)
    private String orderCode;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false)
    private String receiverPhone;

    @Column(name = "pickup_address", nullable = false)
    private String pickupAddress;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(columnDefinition = "TEXT", length = 500)
    private String description;

    @Column(name = "size")
    private String size;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String note;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "distance", nullable = false)
    private Double distance;

    @Column(name = "shipping_fee", nullable = false)
    private Double shippingFee;

    @Column(name = "order_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(name = "delivery_staff_id", nullable = true)
    private Integer deliveryStaffId;
}
