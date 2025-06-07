package com.odms.order.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderFilterResponse {
    private String orderCode;
    private String senderName;
    private String pickupAddress;
    private String receiverName;
    private String receiverPhone;
    private String deliveryAddress;
    private String description;
    private String shippingFee;
    private String orderStatus;

}
