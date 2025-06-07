package com.odms.tracking.dto.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {
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

    // for tracking data from service other
    private Integer customerId;
    private Integer deliveryStaffId;

}
