package com.odms.delivery.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreateEvent {
    private String id;
    private String orderCode;
    private Integer deliveryStaffId;

    private LocalDateTime createdAt;
    private Integer createdBy;
}
