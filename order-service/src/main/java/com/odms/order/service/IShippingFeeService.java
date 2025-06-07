package com.odms.order.service;

import com.odms.order.dto.request.EstimateFeeRequest;
import com.odms.order.dto.response.EstimateFeeResponse;
import com.odms.order.dto.response.ShippingMatrixResponse;

public interface IShippingFeeService {
    ShippingMatrixResponse getShippingMatrix();

    Double calculateShippingFee(Double distance, Double weight);

    EstimateFeeResponse estimateShippingFee(EstimateFeeRequest request);
}
