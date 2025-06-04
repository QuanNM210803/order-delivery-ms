package com.odms.order.dto.request;

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

    @NotBlank(message = "Tên người nhận không được để trống")
    private String receiverName;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    private String receiverPhone;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String deliveryAddress;

    @NotBlank(message = "Địa chỉ lấy hàng không được để trống")
    private String pickupAddress;

    @NotBlank(message = "Mô tả đơn hàng không được để trống")
    private String description;

    private String size;

    @NotNull(message = "Cân nặng đơn hàng không được để trống")
    private Double weight;

    private String note;

    @NotNull(message = "Giá đơn hàng không được để trống")
    private Double price;

}
