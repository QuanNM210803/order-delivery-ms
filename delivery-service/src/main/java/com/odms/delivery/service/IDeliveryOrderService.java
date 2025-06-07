package com.odms.delivery.service;

import com.odms.delivery.dto.event.OrderCreateEvent;
import com.odms.delivery.dto.request.UpdateStatusDeliveryRequest;
import com.odms.delivery.dto.response.IDResponse;
import com.odms.delivery.dto.response.internal.DeliveryInfo;

public interface IDeliveryOrderService {
    void processOrderCreation(OrderCreateEvent orderCreateEvent);

    IDResponse<String> updateDeliveryOrderStatus(UpdateStatusDeliveryRequest request);
    DeliveryInfo getDeliveryOrderStatusHistory(String orderCode);
}
