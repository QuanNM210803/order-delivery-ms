package com.odms.delivery.utils;

import lombok.Getter;

@Getter
public enum Message {
    UPDATE_DELIVERY_ORDER_STATUS_SUCCESS("Cập nhật trạng thái đơn hàng thành công"),

    ;

    private final String message;

    Message(String message) {
        this.message = message;
    }
}
