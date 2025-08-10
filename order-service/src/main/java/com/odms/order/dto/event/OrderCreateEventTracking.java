package com.odms.order.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreateEventTracking {
    private String orderCode;

    private String senderName;
    private String senderPhone;
    private String pickupAddress;

    private String receiverName;
    private String receiverPhone;
    private String deliveryAddress;

    private Double price;
    private Double shippingFee;
    private String description;
    private String note;
    private Double weight;
    private String size;
    private Double distance;

    private List<StatusHistory> statusHistory;

    private Long customerId;

}
