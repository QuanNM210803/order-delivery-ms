package com.odms.delivery.document.enumerate;

import lombok.Getter;

@Getter
public enum ReasonCancel {
    CUSTOMER_CANCEL_BEFORE_ASSIGN("Khách hàng đã hủy đơn hàng"),
    UNREASONABLE_WEIGHT_REPORTED_BY_CUSTOMER("Trọng lượng đơn hàng do khách hàng khai báo không hợp lý"),
    RECIPIENT_REJECTED_PACKAGE( "Người nhận từ chối nhận hàng");

    private final String description;

    ReasonCancel(String description) {
        this.description = description;
    }

}
