package com.odms.delivery.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusHistory {
    private String status;
    private String createdBy;
    private LocalDateTime updatedAt;

    // for cancel status
    private String reasonCancel;
    private String noteCancel;
}
