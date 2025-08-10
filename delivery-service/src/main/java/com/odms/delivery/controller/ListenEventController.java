package com.odms.delivery.controller;

import com.odms.delivery.dto.event.OrderCreateEvent;
import com.odms.delivery.service.IDeliveryOrderService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nmquan.commonlib.utils.ObjectMapperUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListenEventController {
    private final IDeliveryOrderService deliveryOrderService;

    @SneakyThrows
    @KafkaListener(topics = "order-create-topic", groupId = "delivery-service")
    void listenOrderCreateEvent(String message) {
        OrderCreateEvent orderCreateEvent = ObjectMapperUtils.convertToObject(message, OrderCreateEvent.class);
        deliveryOrderService.processOrderCreation(orderCreateEvent);
    }
}
