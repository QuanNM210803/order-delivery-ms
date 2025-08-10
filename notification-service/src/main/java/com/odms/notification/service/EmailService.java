package com.odms.notification.service;

import com.odms.notification.enums.TypeMail;
import com.odms.notification.dto.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import nmquan.commonlib.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.sender}")
    private String SENDER;

    public void handleSendEmail(String notificationJson) {
        NotificationEvent notificationEvent = ObjectMapperUtils.convertToObject(notificationJson, NotificationEvent.class);

        String recipient = notificationEvent.getRecipient();
        String subject = this.getSubject(notificationEvent.getTypeMail());
        String body = this.getBody(notificationEvent.getTypeMail(), notificationEvent.getContent());

        this.sendEmail(recipient, subject, body);
    }

    private String getBody(TypeMail typeMail, String content) {
        switch (typeMail) {
            case REGISTER:
                return "Welcome to ODMS! Your account has been created successfully.\n" + content;
            case FORGOT_PASSWORD:
                return "To reset your password, please follow the instructions provided in the link.\n" + content;
            case ASSIGNED_DELIVERY:
                return "A new delivery has been assigned to you. Please check your dashboard for details.\n" + content;
            case ASSIGNED_CUSTOMER:
                return "Your order has been assigned a driver. Please check your dashboard for details.\n" + content;
            case COMPLETED:
                return "Your order has been delivered successfully.\n" + content;
            case CANCELLED:
                return "Your order has been cancelled.\n" + content;
            default:
                return "You have a new notification from ODMS.\n" + content;
        }
    }

    private String getSubject(TypeMail typeMail) {
        switch (typeMail) {
            case REGISTER:
                return "[ODMS] Welcome To ODMS!";
            case FORGOT_PASSWORD:
                return "[ODMS] Password Reset Request";
            case ASSIGNED_DELIVERY:
                return "[ODMS] New Delivery Assigned to You";
            case ASSIGNED_CUSTOMER:
                return "[ODMS] Order assigned to driver";
            case COMPLETED:
                return "[ODMS] Delivery Order Completed";
            case CANCELLED:
                return "[ODMS] Order Has Been Cancelled";
            default:
                return "[ODMS] Notification";
        }
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SENDER);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

}
