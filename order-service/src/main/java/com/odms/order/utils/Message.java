package com.odms.order.utils;

import lombok.Getter;

@Getter
public enum Message {
    ORDER_CREATED("Đơn hàng đã được tạo thành công"),

    ;

    private final String message;

    Message(String message) {
        this.message = message;
    }
}
