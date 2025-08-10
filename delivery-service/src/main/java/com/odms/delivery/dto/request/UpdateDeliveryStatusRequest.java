package com.odms.delivery.dto.request;

import com.odms.delivery.constant.Message;
import com.odms.delivery.enums.OrderStatus;
import com.odms.delivery.enums.ReasonCancel;
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

    @NotBlank(message = Message.ORDER_CODE_NOT_BLANK)
    private String orderCode;

    @NotNull(message = Message.ORDER_STATUS_NOT_NULL)
    private OrderStatus status;

    private Long deliveryStaffId;

    private ReasonCancel reasonCancel;

    private String noteCancel;
}
