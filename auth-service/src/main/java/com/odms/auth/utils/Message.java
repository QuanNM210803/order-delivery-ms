package com.odms.auth.utils;

import lombok.Getter;

@Getter
public enum Message {
    LOGIN_SUCCESS("Đăng nhập thành công"),
    REGISTER_SUCCESS("Đăng ký thành công"),

    ;

    private final String message;

    Message(String message) {
        this.message = message;
    }
}
