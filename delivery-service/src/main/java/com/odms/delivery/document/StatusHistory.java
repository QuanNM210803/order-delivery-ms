package com.odms.delivery.document;

import com.odms.delivery.document.enumerate.OrderStatus;
import com.odms.delivery.document.enumerate.ReasonCancel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusHistory {
    private OrderStatus status;
    private Integer createdBy;
    private LocalDateTime updatedAt;

    // for cancel status
    private ReasonCancel reasonCancel;
    private String noteCancel;
}
