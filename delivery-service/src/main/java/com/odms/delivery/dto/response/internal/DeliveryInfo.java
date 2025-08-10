package com.odms.delivery.dto.response.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.odms.delivery.dto.event.StatusHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryInfo {
    private Long deliveryStaffId;
    private List<StatusHistory> statusHistory;
}
