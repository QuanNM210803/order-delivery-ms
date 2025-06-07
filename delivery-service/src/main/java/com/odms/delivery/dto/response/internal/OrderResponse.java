package com.odms.delivery.dto.response.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    private Integer orderId;
    private String orderCode;
    private Integer customerId;
    private String receiverName;
    private String receiverPhone;
    private String pickupAddress;
    private String deliveryAddress;
    private String description;
    private String size;
    private Double weight;
    private String note;
    private Double price;
    private Double distance;
    private Double shippingFee;
}
