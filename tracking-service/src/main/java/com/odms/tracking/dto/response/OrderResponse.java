package com.odms.tracking.dto.response;

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
public class OrderResponse {
    private String orderCode;

    private String senderName;
    private String senderPhone;
    private String pickupAddress;

    private String receiverName;
    private String receiverPhone;
    private String deliveryAddress;

    private String price;
    private String shippingFee;
    private String description;
    private String note;
    private String weight;
    private String size;
    private String distance;

    private List<StatusHistoryResponse> statusHistory;
}
