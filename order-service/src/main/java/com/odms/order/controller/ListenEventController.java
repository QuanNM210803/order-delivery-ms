package com.odms.order.controller;

import com.odms.order.dto.event.UpdateDeliveryStatusEvent;
import com.odms.order.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nmquan.commonlib.utils.ObjectMapperUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListenEventController {
    private final IOrderService orderService;

    @SneakyThrows
    @KafkaListener(topics = "update-delivery-status-order-topic", groupId = "order-service")
    public void listenEventUpdateStatus(String message) {
        UpdateDeliveryStatusEvent updateDeliveryStatusEvent = ObjectMapperUtils.convertToObject(message, UpdateDeliveryStatusEvent.class);
        orderService.updateStatusDelivery(updateDeliveryStatusEvent);
    }
}
