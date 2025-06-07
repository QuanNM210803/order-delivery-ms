package com.odms.tracking.controller;

import com.odms.tracking.service.ITrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListenEventController {
    private final ITrackingService trackingService;

    @KafkaListener(topics = "order-create-tracking-topic", groupId = "tracking-service")
    public void listenOrderCreateEvent(String message) {
        trackingService.processOrderCreation(message);
    }

    @KafkaListener(topics = "update-delivery-status-tracking-topic", groupId = "tracking-service")
    public void listenOrderUpdateEvent(String message) {
        trackingService.processOrderUpdate(message);
    }

}
