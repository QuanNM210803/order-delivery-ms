package com.odms.order.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreateEvent {
    private String id;
    private String orderCode;
    private Integer deliveryStaffId;
    private String senderName;
    private String pickupAddress;
    private String receiverName;
    private String receiverPhone;
    private String deliveryAddress;
    private String description;
    private Double shippingFee;

    private LocalDateTime createdAt;
    private Integer createdBy;
}
