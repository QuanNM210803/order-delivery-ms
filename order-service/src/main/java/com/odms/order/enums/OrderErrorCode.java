package com.odms.order.enums;

import com.odms.order.constant.Message;
import nmquan.commonlib.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND(2001, Message.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND),

    ;

    OrderErrorCode(Integer code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final Integer code;
    private final String message;
    private final HttpStatus statusCode;

    @Override
    public HttpStatus getStatusCode() {
        return statusCode;
    }
    @Override
    public Integer getCode() {
        return code;
    }
    @Override
    public String getMessage() {
        return message;
    }
}
