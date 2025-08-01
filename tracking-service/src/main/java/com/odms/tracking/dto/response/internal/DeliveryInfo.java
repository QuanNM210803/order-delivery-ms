package com.odms.tracking.dto.response.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.odms.tracking.dto.event.StatusHistory;
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
    private Integer deliveryStaffId;
    private List<StatusHistory> statusHistory;
}
