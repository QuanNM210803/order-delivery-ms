package com.odms.order.dto.request;

import com.odms.order.constant.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @NotBlank(message = Message.ORDER_RECEIVER_NAME_REQUIRE)
    private String receiverName;

    @NotBlank(message = Message.ORDER_RECEIVER_PHONE_REQUIRE)
    private String receiverPhone;

    @NotBlank(message = Message.ORDER_DELIVERY_ADDRESS_REQUIRE)
    private String deliveryAddress;

    @NotBlank(message = Message.ORDER_PICKUP_ADDRESS_REQUIRE)
    private String pickupAddress;

    @NotBlank(message = Message.ORDER_DESCRIPTION_REQUIRE)
    private String description;

    private String size;

    @NotNull(message = Message.ORDER_WEIGHT_REQUIRE)
    private Double weight;

    private String note;

    @NotNull(message = Message.ORDER_PRICE_REQUIRE)
    private Double price;

}
