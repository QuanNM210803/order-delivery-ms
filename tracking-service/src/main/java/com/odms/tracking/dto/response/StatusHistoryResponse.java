package com.odms.tracking.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusHistoryResponse {
    private String status;
    private String createdBy;
    private String updatedAt;

    // for cancel status
    private String reasonCancel;
    private String noteCancel;
}
