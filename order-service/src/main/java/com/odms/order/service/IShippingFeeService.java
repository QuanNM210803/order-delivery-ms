package com.odms.order.service;

import com.odms.order.dto.response.ShippingMatrixResponse;

public interface IShippingFeeService {
    ShippingMatrixResponse getShippingMatrix();

    Double calculateShippingFee(Double distance, Double weight);
}
