package com.odms.order.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreateEvent {
    private String id;
    private String orderCode;
    private Long deliveryStaffId;

    private Instant createdAt;
    private Long createdBy;
}
