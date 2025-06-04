package com.odms.order.service;

import com.odms.order.dto.request.OrderRequest;
import com.odms.order.dto.response.IDResponse;

public interface IOrderService {
    IDResponse<String> createOrder(OrderRequest orderRequest);
}
