package com.odms.auth.utils;

import lombok.Getter;

@Getter
public enum Message {
    LOGIN_SUCCESS("Đăng nhập thành công"),
    REGISTER_SUCCESS("Đăng ký thành công"),
    VERIFY_EMAIL_SUCCESS("Xác thực email thành công"),
    UPDATE_STATUS_FINDING_ORDER_SUCCESS("Cập nhật trạng thái tìm đơn hàng thành công"),

    ;

    private final String message;

    Message(String message) {
        this.message = message;
    }
}
