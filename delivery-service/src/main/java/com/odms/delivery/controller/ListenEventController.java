package com.odms.delivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odms.delivery.dto.event.OrderCreateEvent;
import com.odms.delivery.service.IDeliveryOrderService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListenEventController {
    private final IDeliveryOrderService deliveryOrderService;

    @SneakyThrows
    @KafkaListener(topics = "order-create-topic", groupId = "delivery-service")
    void listenOrderCreateEvent(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        OrderCreateEvent orderCreateEvent = objectMapper.readValue(message, OrderCreateEvent.class);
        deliveryOrderService.processOrderCreation(orderCreateEvent);
    }
}
