package com.odms.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odms.order.dto.event.UpdateDeliveryStatusEvent;
import com.odms.order.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListenEventController {
    private final IOrderService orderService;

    @SneakyThrows
    @KafkaListener(topics = "update-delivery-status-order-topic", groupId = "order-service")
    public void listenEventUpdateStatus(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        UpdateDeliveryStatusEvent updateDeliveryStatusEvent = objectMapper.readValue(message, UpdateDeliveryStatusEvent.class);
        orderService.updateStatusDelivery(updateDeliveryStatusEvent);
    }
}
