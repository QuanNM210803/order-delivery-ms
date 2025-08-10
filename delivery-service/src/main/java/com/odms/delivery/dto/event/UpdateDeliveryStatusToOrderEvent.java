package com.odms.delivery.dto.event;

import com.odms.delivery.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDeliveryStatusToOrderEvent {
    private String orderCode;
    private OrderStatus status;

    // for update status = ASSIGNED
    private Long deliveryStaffId;
}
