package com.odms.order.dto.request;

import com.odms.order.constant.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstimateFeeRequest {
    @NotBlank(message = Message.ESTIMATE_PICKUP_ADDRESS_REQUIRE)
    String pickupAddress;

    @NotBlank(message = Message.ESTIMATE_DELIVERY_ADDRESS_REQUIRE)
    String deliveryAddress;

    @NotNull(message = Message.ESTIMATE_WEIGHT_REQUIRE)
    Double weight; // kg
}
