package com.odms.delivery.service;

import com.odms.delivery.dto.event.OrderCreateEvent;
import com.odms.delivery.dto.request.UpdateDeliveryStatusRequest;
import com.odms.delivery.dto.response.UpdateDeliveryStatusResponse;
import com.odms.delivery.dto.response.internal.DeliveryInfo;

public interface IDeliveryOrderService {
    void processOrderCreation(OrderCreateEvent orderCreateEvent);

    UpdateDeliveryStatusResponse updateDeliveryOrderStatus(UpdateDeliveryStatusRequest request);
    DeliveryInfo getDeliveryOrderStatusHistory(String orderCode);
}
