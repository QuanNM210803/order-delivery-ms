package com.odms.order.dto.event;

import com.odms.order.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDeliveryStatusEvent {
    private String orderCode;
    private OrderStatus status;

    // for update status = ASSIGNED
    private Long deliveryStaffId;

}
