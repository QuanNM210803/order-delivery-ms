package com.odms.delivery.dto.event;

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
    private StatusHistory statusHistory;

    // for status ASSIGNED
    private Integer deliveryStaffId;
}
