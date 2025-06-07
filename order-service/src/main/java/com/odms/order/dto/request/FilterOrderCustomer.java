package com.odms.order.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.odms.order.entity.enumerate.OrderStatus;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterOrderCustomer extends PageRequest{
    private String orderCode;
    private String receiverName;
    private String receiverPhone;
    private List<OrderStatus> orderStatuses;

    @PastOrPresent(message = "Ngày bắt đầu phải là ngày trong quá khứ hoặc hiện tại")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

}
