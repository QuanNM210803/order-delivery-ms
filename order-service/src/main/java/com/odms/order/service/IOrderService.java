package com.odms.order.service;

import com.odms.order.dto.request.OrderRequest;
import com.odms.order.dto.response.IDResponse;
import com.odms.order.dto.response.OrderResponse;

public interface IOrderService {
    IDResponse<String> createOrder(OrderRequest orderRequest);
    boolean checkCustomerId(Integer customerId, String orderCode);
    OrderResponse getOrderByOrderCode(String orderCode);
}
