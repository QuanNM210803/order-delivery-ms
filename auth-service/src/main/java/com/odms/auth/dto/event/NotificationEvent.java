package com.odms.auth.dto.event;

import com.odms.auth.enums.TypeMail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {
    TypeMail typeMail;
    String recipient;
    String content;
}
