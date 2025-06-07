package com.odms.tracking.service;

import com.odms.tracking.dto.response.OrderResponse;

public interface ITrackingService {
    OrderResponse getOrderDetails(String orderCode, String phone);
    void processOrderCreation(String message);
    void processOrderUpdate(String message);
}
