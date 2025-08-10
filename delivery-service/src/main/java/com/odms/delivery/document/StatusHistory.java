package com.odms.delivery.document;

import com.odms.delivery.enums.OrderStatus;
import com.odms.delivery.enums.ReasonCancel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusHistory {
    private OrderStatus status;
    private Long createdBy;
        private Instant updatedAt;

    // for cancel status
    private ReasonCancel reasonCancel;
    private String noteCancel;
}
