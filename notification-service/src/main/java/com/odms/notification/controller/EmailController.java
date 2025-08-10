package com.odms.notification.controller;

import com.odms.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @KafkaListener(topics = "notification-topic", groupId = "notification-service")
    public void listenNotification(String notificationJson) {
        emailService.handleSendEmail(notificationJson);
    }
}
