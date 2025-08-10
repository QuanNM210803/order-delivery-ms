package com.odms.delivery.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

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
