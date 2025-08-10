package com.odms.delivery.enums;

import lombok.Getter;

@Getter
public enum TypeMail {
    REGISTER(1),
    FORGOT_PASSWORD(2),
    ASSIGNED_DELIVERY(3),
    ASSIGNED_CUSTOMER(4),
    COMPLETED(5),
    CANCELLED(6),
    ;

    private int code;
    TypeMail(int code) {
        this.code = code;
    }
}
