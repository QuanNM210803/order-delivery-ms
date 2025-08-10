package com.odms.tracking.dto.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusHistory {
    private String status;
    private String createdBy;
    private Instant updatedAt;

    // for cancel status
    private String reasonCancel;
    private String noteCancel;
}
