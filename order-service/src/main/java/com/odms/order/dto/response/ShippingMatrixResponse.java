package com.odms.order.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.odms.order.dto.DistanceHeaderDTO;
import com.odms.order.dto.WeightRowDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShippingMatrixResponse {
    private List<DistanceHeaderDTO> distanceHeaders;
    private List<WeightRowDTO> rows;
}
