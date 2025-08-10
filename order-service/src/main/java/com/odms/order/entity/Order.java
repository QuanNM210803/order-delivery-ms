package com.odms.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import nmquan.commonlib.model.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "orders")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {
    @Size(max = 255)
    @NotNull
    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Size(max = 255)
    @NotNull
    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Size(max = 255)
    @NotNull
    @Column(name = "receiver_phone", nullable = false)
    private String receiverPhone;

    @Size(max = 255)
    @NotNull
    @Column(name = "pickup_address", nullable = false)
    private String pickupAddress;

    @Size(max = 255)
    @NotNull
    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Size(max = 255)
    @Column(name = "size")
    private String size;

    @NotNull
    @Column(name = "weight", nullable = false)
    private Double weight;

    @Column(name = "note", length = Integer.MAX_VALUE)
    private String note;

    @NotNull
    @Column(name = "price", nullable = false)
    private Double price;

    @NotNull
    @Column(name = "distance", nullable = false)
    private Double distance;

    @NotNull
    @Column(name = "shipping_fee", nullable = false)
    private Double shippingFee;

    @Size(max = 50)
    @NotNull
    @Column(name = "order_status", nullable = false, length = 50)
    private String orderStatus;

    @Size(max = 255)
    @NotNull
    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(name = "delivery_staff_id")
    private Long deliveryStaffId;

}