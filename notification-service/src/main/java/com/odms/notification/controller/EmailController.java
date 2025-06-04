package com.odms.notification.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.odms.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @KafkaListener(topics = "notification-topic", groupId = "notification-service")
    public void listenNotification(String notificationJson) throws JsonProcessingException {
        emailService.handleSendEmail(notificationJson);
    }
}
