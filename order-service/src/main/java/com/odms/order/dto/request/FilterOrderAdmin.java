package com.odms.order.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.odms.order.constant.Message;
import com.odms.order.enums.OrderStatus;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nmquan.commonlib.constant.CommonConstants;
import nmquan.commonlib.dto.request.FilterRequest;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterOrderAdmin extends FilterRequest {
    private String orderCode;
    private String senderName;
    private String description;
    private List<OrderStatus> orderStatuses;

    @PastOrPresent(message = Message.FILTER_START_DATE_PAST_OR_PRESENT)
    @JsonFormat(pattern = CommonConstants.DATE_TIME.YYYY_MM_DD_HYPHEN)
    private LocalDate startDate;

    @JsonFormat(pattern = CommonConstants.DATE_TIME.YYYY_MM_DD_HYPHEN)
    private LocalDate endDate;

}
