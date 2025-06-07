package com.odms.order.dto.request;

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
    @NotBlank(message = "Yêu cầu nhập địa chỉ lấy hàng")
    String pickupAddress;

    @NotBlank(message = "Yêu cầu nhập địa chỉ giao hàng")
    String deliveryAddress;

    @NotNull(message = "Yêu cầu khối lượng hàng hóa")
    Double weight; // kg
}
