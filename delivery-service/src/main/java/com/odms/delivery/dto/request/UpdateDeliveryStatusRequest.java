package com.odms.delivery.dto.request;

import com.odms.delivery.document.enumerate.OrderStatus;
import com.odms.delivery.document.enumerate.ReasonCancel;
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
public class UpdateDeliveryStatusRequest {

    @NotBlank(message = "Mã đơn hàng không được để trống")
    private String orderCode;

    @NotNull(message = "Trạng thái đơn hàng không được để trống")
    private OrderStatus status;

    private Integer deliveryStaffId;

    private ReasonCancel reasonCancel;

    private String noteCancel;
}
